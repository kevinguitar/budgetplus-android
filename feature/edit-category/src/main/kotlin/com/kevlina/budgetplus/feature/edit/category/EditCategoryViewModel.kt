package com.kevlina.budgetplus.feature.edit.category

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val bubbleRepo: BubbleRepo,
    private val toaster: Toaster,
    private val tracker: Tracker,
    private val stringProvider: StringProvider,
    preferenceHolder: PreferenceHolder,
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

    fun showCategoryExistError(category: String) {
        toaster.showMessage(stringProvider[R.string.category_already_exist, category])
    }
}