package com.kevlina.budgetplus.book.overview.vm

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.auth.AuthManager
import com.kevlina.budgetplus.auth.UserRepo
import com.kevlina.budgetplus.book.bubble.vm.BubbleDest
import com.kevlina.budgetplus.book.bubble.vm.BubbleRepo
import com.kevlina.budgetplus.book.details.RecordsSortMode
import com.kevlina.budgetplus.data.local.PreferenceHolder
import com.kevlina.budgetplus.data.remote.BookRepo
import com.kevlina.budgetplus.data.remote.Record
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.data.remote.TimePeriod
import com.kevlina.budgetplus.data.remote.toAuthor
import com.kevlina.budgetplus.utils.Tracker
import com.kevlina.budgetplus.utils.combineState
import com.kevlina.budgetplus.utils.mapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@Stable
class OverviewViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val userRepo: UserRepo,
    private val bubbleRepo: BubbleRepo,
    private val recordsObserver: RecordsObserver,
    private val tracker: Tracker,
    authManager: AuthManager,
    preferenceHolder: PreferenceHolder
) : ViewModel() {

    val bookName = bookRepo.bookState.mapState { it?.name }

    private var typeCache by preferenceHolder.bindObject(RecordType.Expense)
    private val _type = MutableStateFlow(typeCache)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    // Cache the time period by book id
    private var periodCache by preferenceHolder.bindObject<Map<String, TimePeriod>>(emptyMap())
    private val timePeriodMap = MutableStateFlow(periodCache)

    val timePeriod: StateFlow<TimePeriod> = timePeriodMap.combineState(
        other = bookRepo.bookState,
        scope = viewModelScope
    ) { periodMap, book ->
        book?.id?.let(periodMap::get) ?: TimePeriod.Month
    }

    private var sortModeCache by preferenceHolder.bindObject(RecordsSortMode.Date)
    private val _sortMode = MutableStateFlow(sortModeCache)
    val sortMode: StateFlow<RecordsSortMode> = _sortMode.asStateFlow()

    val isHideAds = authManager.isPremium

    private var isSortingBubbleShown by preferenceHolder.bindBoolean(false)

    val fromDate = timePeriod.mapState { it.from }
    val untilDate = timePeriod.mapState { it.until }

    val totalPrice = combine(recordsObserver.records, type) { records, type ->
        records
            .filter { it.type == type }
            .sumOf { it.price }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val recordGroups: StateFlow<Map<String, List<Record>>> = combine(
        recordsObserver.records,
        type
    ) { records, type ->
        records
            .filter { it.type == type }
            .map { record ->
                val author = record.author?.id?.let(userRepo::getUser)?.toAuthor()
                record.copy(author = author ?: record.author)
            }
            .groupBy { it.category }
            .toList()
            .sortedByDescending { (_, v) -> v.sumOf { it.price } }
            .toMap()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    init {
        combine(
            bookRepo.bookState.mapNotNull { it?.id },
            timePeriod,
            recordsObserver::observeRecords
        ).launchIn(viewModelScope)
    }

    fun setRecordType(type: RecordType) {
        _type.value = type
        typeCache = type
        tracker.logEvent("overview_type_changed")
    }

    fun setTimePeriod(timePeriod: TimePeriod) {
        val bookId = bookRepo.currentBookId ?: return
        val newMapping = periodCache.toMutableMap()
            .apply { this[bookId] = timePeriod }

        timePeriodMap.value = newMapping
        periodCache = newMapping

        tracker.logEvent(
            event = "overview_period_changed",
            params = mapOf("period" to timePeriod::class.simpleName.orEmpty())
        )
    }

    fun setSortMode(sortMode: RecordsSortMode) {
        _sortMode.value = sortMode
        sortModeCache = sortMode
        tracker.logEvent("overview_sort_mode_changed")
    }

    fun highlightSortingButton(dest: BubbleDest) {
        if (!isSortingBubbleShown) {
            isSortingBubbleShown = true
            bubbleRepo.addBubbleToQueue(dest)
        }
    }
}