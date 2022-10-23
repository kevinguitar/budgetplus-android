package com.kevlina.budgetplus.book.overview.vm

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.auth.AuthManager
import com.kevlina.budgetplus.auth.UserRepo
import com.kevlina.budgetplus.data.local.PreferenceHolder
import com.kevlina.budgetplus.data.remote.BookRepo
import com.kevlina.budgetplus.data.remote.Record
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.data.remote.TimePeriod
import com.kevlina.budgetplus.data.remote.User
import com.kevlina.budgetplus.utils.Toaster
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@Stable
class OverviewViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val recordsObserver: RecordsObserver,
    private val tracker: Tracker,
    private val authManager: AuthManager,
    private val userRepo: UserRepo,
    private val toaster: Toaster,
    preferenceHolder: PreferenceHolder,
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

    val isHideAds = authManager.isPremium

    val fromDate = timePeriod.mapState { it.from }
    val untilDate = timePeriod.mapState { it.until }

    val authors = bookRepo.bookState.mapState {
        it?.authors.orEmpty().mapNotNull(userRepo::getUser)
    }

    private val _selectedAuthor = MutableStateFlow<User?>(null)
    val selectedAuthor: StateFlow<User?> = _selectedAuthor.asStateFlow()

    private val records = combine(
        recordsObserver.records,
        type,
        selectedAuthor
    ) { records, type, author ->
        val authorId = author?.id
        records.filter {
            it.type == type && (authorId == null || it.author?.id == authorId)
        }
    }

    val totalPrice = records.map { records -> records.sumOf { it.price } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val balance = records.map { records ->
        val expenses = records.filter { it.type == RecordType.Expense }
        val incomes = records.filter { it.type == RecordType.Income }
        incomes.sumOf { it.price } - expenses.sumOf { it.price }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val recordGroups: StateFlow<Map<String, List<Record>>> = records.map { records ->
        records.groupBy { it.category }
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

        val newMapping = periodCache.toMutableMap()
            .apply { this[bookId] = period }

        timePeriodMap.value = newMapping
        periodCache = newMapping
    }

    fun setAuthor(author: User?) {
        _selectedAuthor.value = author
    }
}