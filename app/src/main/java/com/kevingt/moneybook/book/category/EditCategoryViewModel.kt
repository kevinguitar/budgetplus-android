package com.kevingt.moneybook.book.category

import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.RecordType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val bookRepo: BookRepo
) : ViewModel() {

    fun onCategoryEdited(mode: CategoryEditMode, type: RecordType, name: String) {
        when (mode) {
            CategoryEditMode.Add -> bookRepo.addCategory(type, name)
            is CategoryEditMode.Rename -> bookRepo.editCategory(type, mode.currentName, name)
        }
    }

    fun deleteCategory(mode: CategoryEditMode, type: RecordType) {
        val name = (mode as? CategoryEditMode.Rename)?.currentName ?: return
        bookRepo.deleteCategory(type, name)
    }
}