package com.kevingt.moneybook.book.overview.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.book.bubble.vm.BubbleDest
import com.kevingt.moneybook.book.bubble.vm.BubbleRepo
import com.kevingt.moneybook.book.details.RecordsSortMode
import com.kevingt.moneybook.data.local.PreferenceHolder
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.utils.mapState
import com.kevingt.moneybook.utils.priceText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val bubbleRepo: BubbleRepo,
    authManager: AuthManager,
    preferenceHolder: PreferenceHolder
) : ViewModel() {

    private var typeCache by preferenceHolder.bindObject(RecordType.Expense)
    private val _type = MutableStateFlow(typeCache)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    private var periodCache by preferenceHolder.bindObject<TimePeriod>(TimePeriod.Month)
    private val _timePeriod = MutableStateFlow(periodCache)
    val timePeriod: StateFlow<TimePeriod> = _timePeriod.asStateFlow()

    private var sortModeCache by preferenceHolder.bindObject(RecordsSortMode.Date)
    private val _sortMode = MutableStateFlow(sortModeCache)
    val sortMode: StateFlow<RecordsSortMode> = _sortMode.asStateFlow()

    val isHideAds = authManager.isHideAds

    private var isSortingBubbleShown by preferenceHolder.bindBoolean(false)

    val fromDate = timePeriod.mapState { it.from }
    val untilDate = timePeriod.mapState { it.until }

    private val records = MutableStateFlow<Sequence<Record>?>(null)

    val totalPrice = records.mapState { record ->
        record.orEmpty().sumOf { it.price }.priceText
    }

    val recordGroups = combine(
        records.filterNotNull(),
        bookRepo.bookState.filterNotNull(),
        type,
    ) { records, book, type ->
        val recordsGroup = records
            .groupBy { it.category }
            .toList()
            .sortedByDescending { (_, v) -> v.sumOf { it.price } }
            .toMap()
            .toMutableMap()

        // Put the empty categories at the end
        when (type) {
            RecordType.Expense -> book.expenseCategories
            RecordType.Income -> book.incomeCategories
        }.forEach { category ->
            recordsGroup.putIfAbsent(category, emptyList())
        }

        recordsGroup.toMap()
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    init {
        bookRepo.bookState
            .onEach { observeRecords() }
            .launchIn(viewModelScope)
    }

    fun setRecordType(type: RecordType) {
        _type.value = type
        typeCache = type
        observeRecords()
    }

    fun setTimePeriod(timePeriod: TimePeriod) {
        _timePeriod.value = timePeriod
        periodCache = timePeriod
        observeRecords()
    }

    fun setSortMode(sortMode: RecordsSortMode) {
        _sortMode.value = sortMode
        sortModeCache = sortMode
    }

    fun highlightSortingButton(dest: BubbleDest) {
        if (!isSortingBubbleShown) {
            isSortingBubbleShown = true
            bubbleRepo.setDestination(dest)
        }
    }

    private var recordsRegistration: ListenerRegistration? = null

    private fun observeRecords() {
        recordsRegistration?.remove()
        recordsRegistration = Firebase.firestore
            .collection("books")
            .document(requireNotNull(bookRepo.currentBookId) { "Book id is null" })
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