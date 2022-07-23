package com.kevingt.moneybook.book.category

import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.bubble.vm.BubbleDest
import com.kevingt.moneybook.book.bubble.vm.BubbleRepo
import com.kevingt.moneybook.data.local.PreferenceHolder
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.utils.Toaster
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