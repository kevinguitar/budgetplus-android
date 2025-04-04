package com.kevlina.budgetplus.feature.overview

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.VibratorManager
import com.kevlina.budgetplus.core.common.bundle
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
import com.kevlina.budgetplus.core.data.resolveAuthor
import com.kevlina.budgetplus.core.settings.api.ChartModeViewModel
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import com.kevlina.budgetplus.feature.utils.CsvWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
internal class OverviewViewModel @Inject constructor(
    private val recordRepo: RecordRepo,
    private val recordsObserver: RecordsObserver,
    private val tracker: Tracker,
    private val authManager: AuthManager,
    private val userRepo: UserRepo,
    private val bubbleRepo: BubbleRepo,
    private val csvWriter: CsvWriter,
    private val snackbarSender: SnackbarSender,
    val bookRepo: BookRepo,
    val timeModel: OverviewTimeViewModel,
    val chartModeModel: ChartModeViewModel,
    val vibratorManager: VibratorManager,
    preferenceHolder: PreferenceHolder,
) : ViewModel() {

    val bookName = bookRepo.bookState.mapState { it?.name }
    val isSoloAuthor = bookRepo.bookState.mapState { it?.authors?.size == 1 }

    private var typeCache by preferenceHolder.bindObject(RecordType.Expense)
    private val _type = MutableStateFlow(typeCache)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    private var modeCache by preferenceHolder.bindObject(OverviewMode.AllRecords)
    private val _mode = MutableStateFlow(modeCache)
    val mode: StateFlow<OverviewMode> = _mode.asStateFlow()

    private var isModeBubbleShown by preferenceHolder.bindBoolean(false)
    private var isExportBubbleShown by preferenceHolder.bindBoolean(false)
    private var isTapHintBubbleShown by preferenceHolder.bindBoolean(false)
    private var isPieChartBubbleShown by preferenceHolder.bindBoolean(false)
    private var tapHintBubbleJob: Job? = null
    private var pieChartBubbleJob: Job? = null

    val authors = bookRepo.bookState
        .map {
            withContext(Dispatchers.Default) {
                it?.authors
                    .orEmpty()
                    .mapNotNull(userRepo::getUser)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _selectedAuthor = MutableStateFlow<User?>(null)
    val selectedAuthor: StateFlow<User?> = _selectedAuthor.asStateFlow()

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

    val totalPrice = records.map { records ->
        records.orEmpty().sumOf { it.price }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val totalFormattedPrice = totalPrice.mapState {
        bookRepo.formatPrice(it, alwaysShowSymbol = true)
    }

    val formattedBalance = combine(
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

    val recordList: StateFlow<List<Record>?> = records.map { records ->
        records
            ?.map(userRepo::resolveAuthor)
            ?.sortedByDescending { it.createdOn }
            ?.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val recordGroups: StateFlow<Map<String, List<Record>>?> = records.map { records ->
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

    fun toggleMode() {
        val newMode = when (mode.value) {
            OverviewMode.AllRecords -> OverviewMode.GroupByCategories
            OverviewMode.GroupByCategories -> OverviewMode.AllRecords
        }
        _mode.value = newMode
        modeCache = newMode
        tracker.logEvent("overview_mode_changed")
    }

    fun exportToCsv(context: Context) {
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
                context.startActivity(Intent.createChooser(intent, null))
                snackbarSender.send(R.string.export_csv_success)
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            }
        }
    }

    fun showWriteFilePermissionHint() {
        snackbarSender.send(R.string.permission_hint)
    }

    fun setRecordType(type: RecordType) {
        _type.value = type
        typeCache = type
        tracker.logEvent("overview_type_changed")
    }

    fun setAuthor(author: User?) {
        _selectedAuthor.value = author
    }

    fun canEditRecord(record: Record): Boolean {
        val myUserId = authManager.userState.value?.id
        return bookRepo.bookState.value?.ownerId == myUserId || record.author?.id == myUserId
    }

    fun duplicateRecord(record: Record) {
        recordRepo.duplicateRecord(record)
        snackbarSender.send(R.string.record_duplicated)
    }

    fun onGroupClicked() {
        tracker.logEvent(
            event = "overview_group_clicked",
            params = bundle {
                putString("chart_mode", chartModeModel.chartModeAnalyticsName)
            }
        )
    }

    fun highlightModeButton(dest: BubbleDest) {
        if (isModeBubbleShown) return

        viewModelScope.launch {
            if (recordList.filterNotNull().first().isNotEmpty()) {
                isModeBubbleShown = true
                bubbleRepo.addBubbleToQueue(dest)
            }
        }
    }

    fun highlightExportButton(dest: BubbleDest) {
        if (isExportBubbleShown) return

        viewModelScope.launch {
            if (recordList.filterNotNull().first().isNotEmpty()) {
                isExportBubbleShown = true
                bubbleRepo.addBubbleToQueue(dest)
            }
        }
    }

    fun highlightTapHint(dest: BubbleDest) {
        if (isTapHintBubbleShown) return

        tapHintBubbleJob?.cancel()
        tapHintBubbleJob = viewModelScope.launch {
            delay(animationDelay)

            isTapHintBubbleShown = true
            bubbleRepo.addBubbleToQueue(dest)
        }
    }

    fun highlightPieChart(dest: BubbleDest) {
        if (isPieChartBubbleShown) return

        pieChartBubbleJob?.cancel()
        pieChartBubbleJob = viewModelScope.launch {
            delay(animationDelay)

            isPieChartBubbleShown = true
            bubbleRepo.addBubbleToQueue(dest)
        }
    }

    companion object {
        private val animationDelay = 200.milliseconds
    }
}