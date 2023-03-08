package com.kevlina.budgetplus.feature.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.UserRepo
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import com.kevlina.budgetplus.feature.utils.resolveAuthor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class RecordsViewModel @AssistedInject constructor(
    @Assisted type: RecordType,
    @Assisted("category") val category: String,
    @Assisted("authorId") private val authorId: String?,
    private val userRepo: UserRepo,
    private val bookRepo: BookRepo,
    private val bubbleRepo: BubbleRepo,
    private val tracker: Tracker,
    private val authManager: AuthManager,
    recordsObserver: RecordsObserver,
    preferenceHolder: PreferenceHolder,
) : ViewModel() {

    private var sortModeCache by preferenceHolder.bindObject(RecordsSortMode.Date)
    private val _sortMode = MutableStateFlow(sortModeCache)
    val sortMode: StateFlow<RecordsSortMode> = _sortMode.asStateFlow()

    private var isSortingBubbleShown by preferenceHolder.bindBoolean(false)

    val isHideAds = authManager.isPremium

    val records = combine(recordsObserver.records, sortMode) { records, sortMode ->
        records ?: return@combine null
        records
            .filter {
                it.type == type && it.category == category &&
                    (authorId == null || it.author?.id == authorId)
            }
            .map(userRepo::resolveAuthor)
            .run {
                when (sortMode) {
                    RecordsSortMode.Date -> sortedByDescending { it.createdOn }
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

    fun canEditRecord(record: Record): Boolean {
        val myUserId = authManager.userState.value?.id
        return bookRepo.bookState.value?.ownerId == myUserId || record.author?.id == myUserId
    }

    @AssistedFactory
    interface Factory {
        fun create(
            type: RecordType,
            @Assisted("category") category: String,
            @Assisted("authorId") authorId: String?,
        ): RecordsViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            type: RecordType,
            category: String,
            authorId: String?,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(type, category, authorId) as T
            }
        }
    }
}