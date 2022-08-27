package com.kevlina.budgetplus.book.overview.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.auth.AuthManager
import com.kevlina.budgetplus.book.bubble.vm.BubbleDest
import com.kevlina.budgetplus.book.bubble.vm.BubbleRepo
import com.kevlina.budgetplus.book.details.RecordsSortMode
import com.kevlina.budgetplus.data.local.PreferenceHolder
import com.kevlina.budgetplus.data.remote.BookRepo
import com.kevlina.budgetplus.data.remote.Record
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.data.remote.TimePeriod
import com.kevlina.budgetplus.utils.Tracker
import com.kevlina.budgetplus.utils.combineState
import com.kevlina.budgetplus.utils.mapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val bubbleRepo: BubbleRepo,
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

    private val records = MutableStateFlow<Sequence<Record>?>(null)

    val totalPrice = records.mapState { record ->
        record.orEmpty().sumOf { it.price }
    }

    val recordGroups: StateFlow<Map<String, List<Record>>> = records.mapState { records ->
        records.orEmpty()
            .groupBy { it.category }
            .toList()
            .sortedByDescending { (_, v) -> v.sumOf { it.price } }
            .toMap()
    }

    init {
        combine(
            timePeriod,
            type,
            bookRepo.bookState.mapNotNull { it?.id }
        ) { _, _, bookId -> observeRecords(bookId) }
            .launchIn(viewModelScope)
    }

    fun setRecordType(type: RecordType) {
        _type.value = type
        typeCache = type
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

    private var recordsRegistration: ListenerRegistration? = null

    private fun observeRecords(bookId: String) {
        recordsRegistration?.remove()
        recordsRegistration = Firebase.firestore
            .collection("books")
            .document(bookId)
            .collection("records")
            .whereEqualTo("type", type.value)
            .whereGreaterThanOrEqualTo("date", fromDate.value.toEpochDay())
            .whereLessThanOrEqualTo("date", untilDate.value.toEpochDay())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.e(e, "Listen failed.")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    records.value = snapshot.documents
                        .mapNotNull { doc -> doc.toObject<Record>()?.copy(id = doc.id) }
                        .asSequence()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        recordsRegistration?.remove()
    }
}