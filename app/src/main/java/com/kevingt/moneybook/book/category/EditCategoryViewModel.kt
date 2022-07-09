package com.kevingt.moneybook.book.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.utils.Toaster
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster
) : ViewModel() {

    fun onCategoryEdited(mode: CategoryEditMode, type: RecordType, name: String) {
        viewModelScope.launch {
            try {
                when (mode) {
                    CategoryEditMode.Add -> bookRepo.addCategory(type, name)
                    is CategoryEditMode.Rename -> {
                        bookRepo.editCategory(type, mode.currentName, name)
                    }
                }
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun deleteCategory(mode: CategoryEditMode, type: RecordType) {
        val name = (mode as? CategoryEditMode.Rename)?.currentName ?: return

        viewModelScope.launch {
            try {
                bookRepo.deleteCategory(type, name)
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }
}