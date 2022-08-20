package com.kevlina.budgetplus.book.details

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.data.remote.Record
import com.kevlina.budgetplus.data.remote.RecordRepo
import com.kevlina.budgetplus.utils.Toaster
import com.kevlina.budgetplus.utils.Tracker
import com.kevlina.budgetplus.utils.mapState
import com.kevlina.budgetplus.utils.parseToPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class EditRecordViewModel @Inject constructor(
    private val recordRepo: RecordRepo,
    private val toaster: Toaster,
    private val tracker: Tracker
) : ViewModel() {

    private val editRecord = MutableStateFlow(Record())

    val name = editRecord.mapState { it.name }

    private val _priceText = MutableStateFlow("")
    val priceText: StateFlow<String> = _priceText.asStateFlow()

    fun setRecord(record: Record) {
        editRecord.value = record
        _priceText.value = record.price.toBigDecimal()
            .setScale(2, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
    }

    fun setName(name: String) {
        editRecord.value = editRecord.value.copy(name = name)
    }

    fun setPrice(price: String) {
        _priceText.value = price
    }

    fun editRecord() {
        val newRecord = try {
            editRecord.value.copy(price = priceText.value.parseToPrice)
        } catch (e: Exception) {
            toaster.showError(e)
            return
        }
        recordRepo.editRecord(newRecord.id, newRecord)
        toaster.showMessage(R.string.record_edited)
        tracker.logEvent("record_edited")
    }

    fun deleteRecord() {
        recordRepo.deleteRecord(editRecord.value.id)
        toaster.showMessage(R.string.record_deleted)
        tracker.logEvent("record_deleted")
    }
}