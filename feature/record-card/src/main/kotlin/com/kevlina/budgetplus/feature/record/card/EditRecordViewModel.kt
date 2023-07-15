package com.kevlina.budgetplus.feature.record.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.remote.Record
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
internal class EditRecordViewModel @Inject constructor(
    private val recordRepo: RecordRepo,
    private val toaster: Toaster,
    private val tracker: Tracker,
    private val stringProvider: StringProvider,
) : ViewModel() {

    fun editRecord(
        record: Record,
        newDate: LocalDate,
        newName: String,
        newPriceText: String,
        editBatch: Boolean,
    ) {
        if (editBatch) {
            viewModelScope.launch {
                try {
                    val count = recordRepo.editBatch(record, newDate, newName, newPriceText)
                    toaster.showMessage(stringProvider[R.string.batch_record_edited, count.toString()])
                    tracker.logEvent("record_batch_edited")
                } catch (e: Exception) {
                    toaster.showError(e)
                }
            }
        } else {
            recordRepo.editRecord(record, newDate, newName, newPriceText)
            toaster.showMessage(R.string.record_edited)
            tracker.logEvent("record_edited")
        }
    }

    fun deleteRecord(record: Record, deleteBatch: Boolean = false) {
        if (deleteBatch) {
            viewModelScope.launch {
                try {
                    val count = recordRepo.deleteBatch(record)
                    toaster.showMessage(stringProvider[R.string.batch_record_deleted, count.toString()])
                    tracker.logEvent("record_batch_deleted")
                } catch (e: Exception) {
                    toaster.showError(e)
                }
            }
        } else {
            recordRepo.deleteRecord(record.id)
            toaster.showMessage(stringProvider[R.string.record_deleted, record.name])
            tracker.logEvent("record_deleted")
        }
    }
}