package com.kevingt.moneybook.book.details

import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.R
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.data.remote.RecordRepo
import com.kevingt.moneybook.utils.Toaster
import com.kevingt.moneybook.utils.mapState
import com.kevingt.moneybook.utils.roundUpPrice
import com.kevingt.moneybook.utils.roundUpPriceText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditRecordViewModel @Inject constructor(
    private val recordRepo: RecordRepo,
    private val toaster: Toaster
) : ViewModel() {

    private val editRecord = MutableStateFlow(Record())

    val name = editRecord.mapState { it.name }

    private val _priceText = MutableStateFlow("")
    val priceText: StateFlow<String> = _priceText.asStateFlow()

    fun setRecord(record: Record) {
        editRecord.value = record
        _priceText.value = record.price.roundUpPriceText
    }

    fun setName(name: String) {
        editRecord.value = editRecord.value.copy(name = name)
    }

    fun setPrice(price: String) {
        _priceText.value = price
    }

    fun editRecord() {
        val newRecord = editRecord.value.copy(
            price = priceText.value.toDouble().roundUpPrice
        )
        recordRepo.editRecord(newRecord.id, newRecord)
        toaster.showMessage(R.string.record_edited)
    }

    fun deleteRecord() {
        recordRepo.deleteRecord(editRecord.value.id)
        toaster.showMessage(R.string.record_deleted)
    }
}