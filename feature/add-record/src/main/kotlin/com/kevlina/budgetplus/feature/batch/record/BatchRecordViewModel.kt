package com.kevlina.budgetplus.feature.batch.record

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BatchFrequency
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.toAuthor
import com.kevlina.budgetplus.feature.add.record.CalculatorViewModel.Companion.EMPTY_PRICE
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@Stable
internal class BatchRecordViewModel @Inject constructor(
    private val recordRepo: RecordRepo,
    private val authManager: AuthManager,
    private val toaster: Toaster,
    private val tracker: Tracker,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _type = MutableStateFlow(RecordType.Expense)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    private val _startDate = MutableStateFlow(LocalDate.now())
    val startDate: StateFlow<LocalDate> = _startDate.asStateFlow()

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    private val _priceText = MutableStateFlow("")
    val priceText: StateFlow<String> = _priceText.asStateFlow()

    private val _frequency = MutableStateFlow(BatchFrequency.Monthly)
    val frequency: StateFlow<BatchFrequency> = _frequency.asStateFlow()

    val batchTimes = (2..30).toList()
    private val _times = MutableStateFlow(batchTimes.first())
    val times: StateFlow<Int> = _times.asStateFlow()

    private val _recordEvent = MutableEventFlow<Unit>()
    val recordEvent: EventFlow<Unit> = _recordEvent.asStateFlow()

    val isBatchButtonEnabled: StateFlow<Boolean> = combine(
        category,
        priceText
    ) { category, priceText ->
        category != null && priceText.isNotEmpty() && priceText != EMPTY_PRICE
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun setType(type: RecordType) {
        _type.value = type
    }

    fun setStartDate(date: LocalDate) {
        _startDate.value = date
    }

    fun setCategory(category: String) {
        _category.value = category
    }

    fun setNote(note: String) {
        _note.value = note
    }

    fun setPriceText(priceText: String) {
        _priceText.value = priceText
    }

    fun setFrequency(frequency: BatchFrequency) {
        _frequency.value = frequency
    }

    fun setTimes(times: Int) {
        _times.value = times
    }

    fun batchRecord() {
        val category = category.value ?: return
        val price = try {
            priceText.value.toDouble()
        } catch (e: Exception) {
            toaster.showError(e)
            return
        }

        val record = Record(
            type = type.value,
            category = category,
            name = note.value.trim().ifEmpty { category },
            price = price,
            author = authManager.userState.value?.toAuthor()
        )

        recordRepo.batchRecord(
            record = record,
            startDate = startDate.value,
            frequency = frequency.value,
            times = times.value
        )
        _recordEvent.sendEvent()
        toaster.showMessage(context.getString(R.string.batch_record_created, times.value.toString(), category))
        tracker.logEvent("record_batched")
        resetScreen()
    }

    private fun resetScreen() {
        _category.value = null
        _note.value = ""
        _priceText.value = EMPTY_PRICE
    }
}