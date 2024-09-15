package com.kevlina.budgetplus.feature.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.combineState
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.common.nav.HistoryDest
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.UserRepo
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.resolveAuthor
import com.kevlina.budgetplus.core.ui.SnackbarSender
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = RecordsViewModel.Factory::class)
class RecordsViewModel @AssistedInject constructor(
    @Assisted private val params: HistoryDest.Records,
    val bookRepo: BookRepo,
    private val userRepo: UserRepo,
    private val recordRepo: RecordRepo,
    private val bubbleRepo: BubbleRepo,
    private val tracker: Tracker,
    private val snackbarSender: SnackbarSender,
    private val authManager: AuthManager,
    recordsObserver: RecordsObserver,
    preferenceHolder: PreferenceHolder,
) : ViewModel() {

    private var sortModeCache by preferenceHolder.bindObject(RecordsSortMode.Date)
    private val _sortMode = MutableStateFlow(sortModeCache)
    val sortMode: StateFlow<RecordsSortMode> = _sortMode.asStateFlow()

    private var isSortingBubbleShown by preferenceHolder.bindBoolean(false)

    private val authorId get() = params.authorId

    // Assuming the records are ready when the records page is opened, and resolve the
    // categories right away to ease the database observation handling.
    private val categories = recordsObserver.records.value
        .orEmpty()
        .filter { it.type == params.type && (authorId == null || it.author?.id == authorId) }
        .groupBy { it.category }
        .toList()
        .sortedByDescending { (_, v) -> v.sumOf { it.price } }
        .map { it.first }

    val initialPage = categories.indexOf(params.category).coerceAtLeast(0)
    val pageSize get() = categories.size

    private val pageIndex = MutableStateFlow(initialPage)

    val category = pageIndex.mapState { categories[it] }

    val recordsList = combine(
        recordsObserver.records,
        sortMode
    ) { allRecords, sortMode ->
        allRecords ?: return@combine null
        categories.map { category ->
            allRecords
                .filter { record ->
                    record.category == category &&
                        record.type == params.type &&
                        (authorId == null || record.author?.id == authorId)
                }
                .map(userRepo::resolveAuthor)
                .run {
                    when (sortMode) {
                        RecordsSortMode.Date -> sortedByDescending { it.createdOn }
                        RecordsSortMode.Price -> sortedByDescending { it.price }
                    }
                }
                .toList()
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val totalPrice = recordsList.combineState(pageIndex, viewModelScope) { recordsList, pageIndex ->
        val total = recordsList?.getOrNull(pageIndex).orEmpty().sumOf { it.price }
        bookRepo.formatPrice(total, alwaysShowSymbol = true)
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

    fun canEditRecord(record: Record): Boolean {
        val myUserId = authManager.userState.value?.id
        return bookRepo.bookState.value?.ownerId == myUserId || record.author?.id == myUserId
    }

    fun setPageIndex(index: Int) {
        if (pageIndex.value != index) {
            pageIndex.value = index
            tracker.logEvent("overview_records_swiped")
        }
    }

    fun duplicateRecord(record: Record) {
        recordRepo.duplicateRecord(record)
        snackbarSender.send(R.string.record_duplicated)
    }

    @AssistedFactory
    interface Factory {
        fun create(params: HistoryDest.Records): RecordsViewModel
    }
}