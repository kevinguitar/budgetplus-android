package com.kevlina.budgetplus.book.details

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.Tracker
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.utils.Toaster
import com.kevlina.budgetplus.utils.parseToPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditRecordViewModel @Inject constructor(
    private val recordRepo: RecordRepo,
    private val toaster: Toaster,
    private val tracker: Tracker,
) : ViewModel() {

    fun editRecord(
        record: Record,
        newDate: LocalDate,
        newName: String,
        newPriceText: String,
    ) {
        val newRecord = try {
            record.copy(
                date = newDate.toEpochDay(),
                name = newName,
                price = newPriceText.parseToPrice
            )
        } catch (e: Exception) {
            toaster.showError(e)
            return
        }
        recordRepo.editRecord(newRecord.id, newRecord)
        toaster.showMessage(R.string.record_edited)
        tracker.logEvent("record_edited")
    }

    fun deleteRecord(recordId: String) {
        recordRepo.deleteRecord(recordId)
        toaster.showMessage(R.string.record_deleted)
        tracker.logEvent("record_deleted")
    }
}