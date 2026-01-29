package com.kevlina.budgetplus.feature.record.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.di.ViewModelKey
import com.kevlina.budgetplus.core.common.di.ViewModelScope
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.feature.category.pills.CategoriesViewModel
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@ViewModelKey(EditRecordViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
class EditRecordViewModel(
    val categoriesVm: CategoriesViewModel,
    private val recordRepo: RecordRepo,
    private val bookRepo: BookRepo,
    private val snackbarSender: SnackbarSender,
    private val stringProvider: StringProvider,
) : ViewModel() {

    val canAddCategory: Boolean
        get() = bookRepo.canEdit

    fun editRecord(
        record: Record,
        newDate: LocalDate,
        newCategory: String,
        newName: String,
        newPriceText: String,
        editBatch: Boolean,
    ) {
        if (editBatch) {
            viewModelScope.launch {
                try {
                    val count = recordRepo.editBatch(
                        oldRecord = record,
                        newDate = newDate,
                        newCategory = newCategory,
                        newName = newName,
                        newPriceText = newPriceText
                    )
                    snackbarSender.send(stringProvider[R.string.batch_record_edited, count.toString()])
                } catch (e: Exception) {
                    snackbarSender.sendError(e)
                }
            }
        } else {
            recordRepo.editRecord(
                oldRecord = record,
                newDate = newDate,
                newCategory = newCategory,
                newName = newName,
                newPriceText = newPriceText
            )
            snackbarSender.send(R.string.record_edited)
        }
    }

    fun deleteRecord(record: Record, deleteBatch: Boolean = false) {
        if (deleteBatch) {
            viewModelScope.launch {
                try {
                    val count = recordRepo.deleteBatch(record)
                    snackbarSender.send(stringProvider[R.string.batch_record_deleted, count.toString()])
                } catch (e: Exception) {
                    snackbarSender.sendError(e)
                }
            }
        } else {
            recordRepo.deleteRecord(record.id)
            snackbarSender.send(stringProvider[R.string.record_deleted, record.name])
        }
    }

    fun addCategory(type: RecordType, newCategory: String) {
        bookRepo.addCategory(type = type, category = newCategory, source = "edit")
    }
}