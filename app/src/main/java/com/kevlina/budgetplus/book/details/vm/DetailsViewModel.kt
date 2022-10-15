package com.kevlina.budgetplus.book.details.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.auth.AuthManager
import com.kevlina.budgetplus.auth.UserRepo
import com.kevlina.budgetplus.book.bubble.vm.BubbleDest
import com.kevlina.budgetplus.book.bubble.vm.BubbleRepo
import com.kevlina.budgetplus.book.details.RecordsSortMode
import com.kevlina.budgetplus.book.overview.vm.RecordsObserver
import com.kevlina.budgetplus.data.local.PreferenceHolder
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.data.remote.toAuthor
import com.kevlina.budgetplus.utils.Tracker
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class DetailsViewModel @AssistedInject constructor(
    @Assisted type: RecordType,
    @Assisted val category: String,
    private val userRepo: UserRepo,
    private val bubbleRepo: BubbleRepo,
    private val tracker: Tracker,
    authManager: AuthManager,
    recordsObserver: RecordsObserver,
    preferenceHolder: PreferenceHolder,
) : ViewModel() {

    private var sortModeCache by preferenceHolder.bindObject(RecordsSortMode.Date)
    private val _sortMode = MutableStateFlow(sortModeCache)
    val sortMode: StateFlow<RecordsSortMode> = _sortMode.asStateFlow()

    private var isSortingBubbleShown by preferenceHolder.bindBoolean(false)

    val isHideAds = authManager.isPremium

    val records = combine(recordsObserver.records, sortMode) { records, sortMode ->
        records
            .filter { it.type == type && it.category == category }
            .map { record ->
                val author = record.author?.id?.let(userRepo::getUser)?.toAuthor()
                record.copy(author = author ?: record.author)
            }
            .run {
                when (sortMode) {
                    RecordsSortMode.Date -> sortedByDescending { it.date }
                    RecordsSortMode.Price -> sortedByDescending { it.price }
                }
            }
            .toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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

    @AssistedFactory
    interface Factory {
        fun create(type: RecordType, category: String): DetailsViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            type: RecordType,
            category: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(type, category) as T
            }
        }
    }
}