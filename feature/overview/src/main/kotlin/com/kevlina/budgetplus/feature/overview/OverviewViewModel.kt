package com.kevlina.budgetplus.feature.overview

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.common.mediumFormatted
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.UserRepo
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import com.kevlina.budgetplus.feature.utils.CsvWriter
import com.kevlina.budgetplus.feature.utils.resolveAuthor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@Stable
internal class OverviewViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val recordsObserver: RecordsObserver,
    private val tracker: Tracker,
    private val authManager: AuthManager,
    private val userRepo: UserRepo,
    private val bubbleRepo: BubbleRepo,
    private val csvWriter: CsvWriter,
    private val toaster: Toaster,
    preferenceHolder: PreferenceHolder,
) : ViewModel() {

    val bookName = bookRepo.bookState.mapState { it?.name }

    private var typeCache by preferenceHolder.bindObject(RecordType.Expense)
    private val _type = MutableStateFlow(typeCache)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    private var modeCache by preferenceHolder.bindObject(OverviewMode.AllRecords)
    private val _mode = MutableStateFlow(modeCache)
    val mode: StateFlow<OverviewMode> = _mode.asStateFlow()

    private var isModeBubbleShown by preferenceHolder.bindBoolean(false)
    private var isExportBubbleShown by preferenceHolder.bindBoolean(false)

    val timePeriod = recordsObserver.timePeriod
    val isHideAds = authManager.isPremium

    val fromDate = timePeriod.mapState { it.from }
    val untilDate = timePeriod.mapState { it.until }

    val authors = bookRepo.bookState.mapState {
        it?.authors.orEmpty().mapNotNull(userRepo::getUser)
    }

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

    val totalPrice = records.map { records -> records.orEmpty().sumOf { it.price } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val balance = combine(
        recordsObserver.records.filterNotNull(),
        selectedAuthor
    ) { records, author ->
        val authorId = author?.id
        withContext(Dispatchers.Default) {
            records
                .filter { authorId == null || it.author?.id == authorId }
                .sumOf { record ->
                    when (record.type) {
                        RecordType.Expense -> -record.price
                        RecordType.Income -> record.price
                    }
                }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val recordList: StateFlow<List<Record>?> = records.map { records ->
        records
            ?.map(userRepo::resolveAuthor)
            ?.sortedByDescending { it.createdOn }
            ?.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val recordGroups: StateFlow<Map<String, List<Record>>?> = records.map { records ->
        records ?: return@map null
        withContext(Dispatchers.Default) {
            records.groupBy { it.category }
                .toList()
                .sortedByDescending { (_, v) -> v.sumOf { it.price } }
                .toMap()
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

    fun exportToCsv() {
        tracker.logEvent("overview_export_to_csv")
        viewModelScope.launch {
            try {
                val name = "${bookName.value}_${fromDate.value.mediumFormatted}_${untilDate.value.mediumFormatted}"
                csvWriter.writeRecordsToCsv(name)
                toaster.showMessage(R.string.export_csv_success)
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun showWriteFilePermissionHint() {
        toaster.showMessage(R.string.permission_hint)
    }

    fun setRecordType(type: RecordType) {
        _type.value = type
        typeCache = type
        tracker.logEvent("overview_type_changed")
    }

    fun setTimePeriod(timePeriod: TimePeriod) {
        val bookId = bookRepo.currentBookId ?: return
        val isAboveOneMonth = timePeriod.from.isBefore(timePeriod.until.minusMonths(1))
        val period = if (!authManager.isPremium.value && isAboveOneMonth) {
            toaster.showMessage(R.string.overview_exceed_max_period)
            tracker.logEvent("overview_exceed_max_period")
            TimePeriod.Custom(
                from = timePeriod.from,
                until = timePeriod.from.plusMonths(1)
            )
        } else {
            timePeriod
        }

        recordsObserver.setTimePeriod(bookId, period)
    }

    fun setAuthor(author: User?) {
        _selectedAuthor.value = author
    }

    fun canEditRecord(record: Record): Boolean {
        val myUserId = authManager.userState.value?.id
        return bookRepo.bookState.value?.ownerId == myUserId || record.author?.id == myUserId
    }

    fun highlightModeButton(dest: BubbleDest) {
        if (!recordList.value.isNullOrEmpty() && !isModeBubbleShown) {
            isModeBubbleShown = true
            bubbleRepo.addBubbleToQueue(dest)
        }
    }

    fun highlightExportButton(dest: BubbleDest) {
        if (!recordList.value.isNullOrEmpty() && !isExportBubbleShown) {
            isExportBubbleShown = true
            bubbleRepo.addBubbleToQueue(dest)
        }
    }
}