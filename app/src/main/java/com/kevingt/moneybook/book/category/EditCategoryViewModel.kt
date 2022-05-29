package com.kevingt.moneybook.book.category

import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.RecordType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val bookRepo: BookRepo
) : ViewModel() {

    val expenseCategories = bookRepo.bookState
        .map { it?.expenseCategories.orEmpty() }

    val incomeCategories = bookRepo.bookState
        .map { it?.incomeCategories.orEmpty() }

    fun onCategoryEdited(mode: CategoryEditMode, type: RecordType, name: String) {
        when (mode) {
            CategoryEditMode.Add -> bookRepo.addCategory(type, name)
            is CategoryEditMode.Rename -> bookRepo.editCategory(type, mode.currentName, name)
        }
    }
}