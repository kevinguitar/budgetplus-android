package com.kevlina.budgetplus.book.category

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.book.bubble.vm.BubbleDest
import com.kevlina.budgetplus.book.bubble.vm.BubbleRepo
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.Tracker
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val bubbleRepo: BubbleRepo,
    private val toaster: Toaster,
    private val tracker: Tracker,
    preferenceHolder: PreferenceHolder
) : ViewModel() {

    val expenseCategories
        get() = bookRepo.bookState.value?.expenseCategories.orEmpty()

    val incomeCategories
        get() = bookRepo.bookState.value?.incomeCategories.orEmpty()

    private var isEditHintBubbleShown by preferenceHolder.bindBoolean(false)
    private var isSaveBubbleShown by preferenceHolder.bindBoolean(false)

    fun updateCategories(type: RecordType, newCategories: List<String>) {
        bookRepo.updateCategories(type, newCategories)
        toaster.showMessage(R.string.category_edit_successful)
        tracker.logEvent("categories_updated")
    }

    fun highlightCategoryHint(dest: BubbleDest) {
        if (!isEditHintBubbleShown) {
            isEditHintBubbleShown = true
            bubbleRepo.addBubbleToQueue(dest)
        }
    }

    fun highlightSaveButton(dest: BubbleDest) {
        if (!isSaveBubbleShown) {
            isSaveBubbleShown = true
            bubbleRepo.addBubbleToQueue(dest)
        }
    }
}