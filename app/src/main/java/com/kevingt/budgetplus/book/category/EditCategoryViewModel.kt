package com.kevingt.budgetplus.book.category

import androidx.lifecycle.ViewModel
import com.kevingt.budgetplus.R
import com.kevingt.budgetplus.book.bubble.vm.BubbleDest
import com.kevingt.budgetplus.book.bubble.vm.BubbleRepo
import com.kevingt.budgetplus.data.local.PreferenceHolder
import com.kevingt.budgetplus.data.remote.BookRepo
import com.kevingt.budgetplus.data.remote.RecordType
import com.kevingt.budgetplus.utils.Toaster
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val bubbleRepo: BubbleRepo,
    private val toaster: Toaster,
    preferenceHolder: PreferenceHolder
) : ViewModel() {

    val expenseCategories
        get() = bookRepo.bookState.value?.expenseCategories.orEmpty()

    val incomeCategories
        get() = bookRepo.bookState.value?.incomeCategories.orEmpty()

    private var isSaveBubbleShown by preferenceHolder.bindBoolean(false)

    fun updateCategories(type: RecordType, newCategories: List<String>) {
        bookRepo.updateCategories(type, newCategories)
        toaster.showMessage(R.string.category_edit_successful)
    }

    fun highlightSaveButton(dest: BubbleDest) {
        if (!isSaveBubbleShown) {
            isSaveBubbleShown = true
            bubbleRepo.setDestination(dest)
        }
    }
}