package com.kevlina.budgetplus.feature.overview

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.export_csv_success
import budgetplus.core.common.generated.resources.permission_hint
import com.kevlina.budgetplus.core.common.ActivityProvider
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.VibratorManager
import com.kevlina.budgetplus.core.common.bundle
import com.kevlina.budgetplus.core.common.di.ViewModelKey
import com.kevlina.budgetplus.core.common.di.ViewModelScope
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.common.mediumFormatted
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.UserRepo
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.data.remote.createdOn
import com.kevlina.budgetplus.core.data.resolveAuthor
import com.kevlina.budgetplus.core.settings.api.ChartModeViewModel
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import com.kevlina.budgetplus.feature.overview.ui.OverviewContentState
import com.kevlina.budgetplus.feature.overview.ui.OverviewHeaderState
import com.kevlina.budgetplus.feature.overview.ui.OverviewListState
import com.kevlina.budgetplus.feature.overview.ui.toState
import com.kevlina.budgetplus.feature.utils.CsvWriter
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@ViewModelKey(OverviewViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
class OverviewViewModel private constructor(
    private val recordRepo: RecordRepo,
    private val recordsObserver: RecordsObserver,
    private val tracker: Tracker,
    private val authManager: AuthManager,
    private val activityProvider: ActivityProvider,
    private val userRepo: UserRepo,
    private val bubbleRepo: BubbleRepo,
    private val csvWriter: CsvWriter,
    private val snackbarSender: SnackbarSender,
    val bookRepo: BookRepo,
    val timeModel: OverviewTimeViewModel,
    val chartModeModel: ChartModeViewModel,
    val vibratorManager: VibratorManager,
    private val preferenceHolder: PreferenceHolder,
) : ViewModel() {

    val bookName = bookRepo.bookState.mapState { it?.name }
    private val isSoloAuthor = bookRepo.bookState.mapState { it?.authors?.size == 1 }

    private val typeFlow = preferenceHolder.bindObject(RecordType.Expense)
    private val type = MutableStateFlow(runBlocking { typeFlow.getValue(this, ::typeFlow).first() })

    private val modeFlow = preferenceHolder.bindObject(OverviewMode.AllRecords)
    internal val mode: MutableStateFlow<OverviewMode>
        field = MutableStateFlow(runBlocking { modeFlow.getValue(this, ::modeFlow).first() })

    private var modeBubbleJob: Job? = null
    private var exportBubbleJob: Job? = null
    private var tapHintBubbleJob: Job? = null
    private var pieChartBubbleJob: Job? = null

    private val authors = bookRepo.bookState
        .map {
            withContext(Dispatchers.Default) {
                it?.authors
                    .orEmpty()
                    .mapNotNull(userRepo::getUser)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val selectedAuthor = MutableStateFlow<User?>(null)

    private val records: Flow<Sequence<Record>?> = combine(
        recordsObserver.records,
        type,
        selectedAuthor
    ) { records, type, author ->
        val authorId = author?.id
        records?.filter {
            it.type == type && (authorId == null || it.author?.id == authorId)
        }
    }

    private val totalPrice = records.map { records ->
        records.orEmpty().sumOf { it.price }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    private val totalFormattedPrice = totalPrice.mapState {
        bookRepo.formatPrice(it, alwaysShowSymbol = true)
    }

    private val formattedBalance = combine(
        recordsObserver.records.filterNotNull(),
        selectedAuthor
    ) { records, author ->
        val authorId = author?.id
        val sum = withContext(Dispatchers.Default) {
            records
                .filter { authorId == null || it.author?.id == authorId }
                .sumOf { record ->
                    when (record.type) {
                        RecordType.Expense -> -record.price
                        RecordType.Income -> record.price
                    }
                }
        }
        bookRepo.formatPrice(sum, alwaysShowSymbol = true)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    private val recordList: StateFlow<List<Record>?> = records.map { records ->
        records
            ?.map(userRepo::resolveAuthor)
            ?.sortedByDescending { it.createdOn }
            ?.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val recordGroups: StateFlow<Map<String, List<Record>>?> = records.map { records ->
        records ?: return@map null
        withContext(Dispatchers.Default) {
            records
                .groupBy { it.category }
                .toList()
                .sortedByDescending { (_, v) -> v.sumOf { it.price } }
                .toMap()
                .mapValues { it.value }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    internal val state = OverviewContentState(
        headerState = OverviewHeaderState(
            type = type,
            totalPrice = totalFormattedPrice,
            balance = formattedBalance,
            recordGroups = recordGroups,
            authors = authors,
            selectedAuthor = selectedAuthor,
            timePeriodSelectorState = timeModel.toState(),
            setRecordType = ::setRecordType,
            setAuthor = ::setAuthor
        ),
        listState = OverviewListState(
            mode = mode,
            chartMode = chartModeModel.chartMode,
            type = type,
            selectedAuthor = selectedAuthor,
            totalPrice = totalPrice,
            recordList = recordList,
            recordGroups = recordGroups,
            isSoloAuthor = isSoloAuthor,
            highlightTapHint = ::highlightTapHint,
            highlightPieChart = ::highlightPieChart,
            formatPrice = bookRepo::formatPrice,
            canEditRecord = ::canEditRecord,
            duplicateRecord = ::duplicateRecord,
            onGroupClicked = ::onGroupClicked
        )
    )

    fun toggleMode() {
        val newMode = when (mode.value) {
            OverviewMode.AllRecords -> OverviewMode.GroupByCategories
            OverviewMode.GroupByCategories -> OverviewMode.AllRecords
        }
        mode.value = newMode
        viewModelScope.launch { preferenceHolder.setObject("modeCache", newMode) }
        tracker.logEvent("overview_mode_changed")
    }

    fun exportToCsv() {
        val activity = activityProvider.currentActivity ?: return
        tracker.logEvent("overview_export_to_csv")
        viewModelScope.launch {
            try {
                val period = recordsObserver.timePeriod.first()
                val name = "${bookName.value}_${period.from.mediumFormatted}_${period.until.mediumFormatted}"
                val fileUri = csvWriter.writeRecordsToCsv(name)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = csvWriter.mimeType
                }
                activity.startActivity(Intent.createChooser(intent, null))
                snackbarSender.send(Res.string.export_csv_success)
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            }
        }
    }

    fun showWriteFilePermissionHint() {
        viewModelScope.launch { snackbarSender.send(Res.string.permission_hint) }
    }

    private fun setRecordType(newType: RecordType) {
        type.value = newType
        typeCache = newType
        tracker.logEvent("overview_type_changed")
    }

    private fun setAuthor(author: User?) {
        selectedAuthor.value = author
    }

    private fun canEditRecord(record: Record): Boolean {
        return bookRepo.canEdit || record.author?.id == authManager.userId
    }

    private fun duplicateRecord(record: Record) {
        recordRepo.duplicateRecord(record)
    }

    private fun onGroupClicked() {
        tracker.logEvent(
            event = "overview_group_clicked",
            params = bundle {
                putString("chart_mode", chartModeModel.chartModeAnalyticsName)
            }
        )
    }

    fun highlightModeButton(dest: BubbleDest) {
        if (modeBubbleJob != null) return
        modeBubbleJob = viewModelScope.launch {
            if (recordList.filterNotNull().first().isNotEmpty()) {
                bubbleRepo.addBubbleToQueue(dest)
            }
        }
    }

    fun highlightExportButton(dest: BubbleDest) {
        if (exportBubbleJob != null) return
        exportBubbleJob = viewModelScope.launch {
            if (recordList.filterNotNull().first().isNotEmpty()) {
                bubbleRepo.addBubbleToQueue(dest)
            }
        }
    }

    private fun highlightTapHint(dest: BubbleDest) {
        if (tapHintBubbleJob != null) return
        tapHintBubbleJob = viewModelScope.launch {
            delay(animationDelay)
            bubbleRepo.addBubbleToQueue(dest)
        }
    }

    private fun highlightPieChart(dest: BubbleDest) {
        if (pieChartBubbleJob != null) return
        pieChartBubbleJob = viewModelScope.launch {
            delay(animationDelay)
            bubbleRepo.addBubbleToQueue(dest)
        }
    }

    companion object {
        private val animationDelay = 200.milliseconds
    }
}