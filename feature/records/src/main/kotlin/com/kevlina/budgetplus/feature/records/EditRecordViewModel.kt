package com.kevlina.budgetplus.feature.records

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.parseToPrice
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.remote.Record
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
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
        // Keep the record's original time
        val originalTime = LocalDateTime
            .ofEpochSecond(record.createdOn, 0, ZoneOffset.UTC)
            .toLocalTime()

        val newRecord = try {
            record.copy(
                date = newDate.toEpochDay(),
                timestamp = LocalDateTime.of(newDate, originalTime).toEpochSecond(ZoneOffset.UTC),
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