package com.kevlina.budgetplus.feature.batch.record

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.EventTrigger
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BatchFrequency
import com.kevlina.budgetplus.core.data.BatchUnit
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.toAuthor
import com.kevlina.budgetplus.feature.add.record.CalculatorViewModel.Companion.EMPTY_PRICE
import com.kevlina.budgetplus.feature.add.record.RecordDateState
import com.kevlina.budgetplus.feature.category.pills.CategoriesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
internal class BatchRecordViewModel @Inject constructor(
    val categoriesVm: CategoriesViewModel,
    val bookRepo: BookRepo,
    private val recordRepo: RecordRepo,
    private val authManager: AuthManager,
    private val snackbarSender: SnackbarSender,
    private val stringProvider: StringProvider,
) : ViewModel() {

    private val _type = MutableStateFlow(RecordType.Expense)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    private val _startRecordDate = MutableStateFlow<RecordDateState>(RecordDateState.Now)
    val startRecordDate: StateFlow<RecordDateState> = _startRecordDate.asStateFlow()

    val note = TextFieldState()
    val priceText = TextFieldState()

    private val _frequency = MutableStateFlow(BatchFrequency(duration = 1, unit = BatchUnit.Month))
    val frequency: StateFlow<BatchFrequency> = _frequency.asStateFlow()

    val batchTimes = (BATCH_TIMES_MIN..BATCH_TIMES_MAX).toList()
    private val _times = MutableStateFlow(batchTimes.first())
    val times: StateFlow<Int> = _times.asStateFlow()

    val recordEvent = EventTrigger<Unit>()

    val isBatchButtonEnabled: StateFlow<Boolean> = combine(
        categoriesVm.category,
        snapshotFlow { priceText.text }
    ) { category, priceText ->
        category != null && priceText.isNotEmpty() && priceText != EMPTY_PRICE
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun setType(type: RecordType) {
        _type.value = type
    }

    fun setStartDate(date: LocalDate) {
        _startRecordDate.value = if (date.isEqual(LocalDate.now())) {
            RecordDateState.Now
        } else {
            RecordDateState.Other(date)
        }
    }

    fun setDuration(duration: Int) {
        _frequency.update { it.copy(duration = duration) }
    }

    fun setUnit(unit: BatchUnit) {
        _frequency.update { it.copy(unit = unit) }
    }

    fun setTimes(times: Int) {
        _times.value = times
    }

    fun batchRecord() {
        val category = categoriesVm.category.value ?: return
        val price = try {
            priceText.text.toString().toDouble()
        } catch (e: Exception) {
            snackbarSender.sendError(e)
            return
        }

        val record = Record(
            type = type.value,
            category = category,
            name = note.text.trim().ifEmpty { category }.toString(),
            price = price,
            author = authManager.userState.value?.toAuthor()
        )

        recordRepo.batchRecord(
            record = record,
            startDate = startRecordDate.value.date,
            frequency = frequency.value,
            times = times.value
        )
        recordEvent.sendEvent(Unit)
        snackbarSender.send(stringProvider[R.string.batch_record_created, times.value.toString(), category])
        resetScreen()
    }

    private fun resetScreen() {
        categoriesVm.setCategory(null)
        note.clearText()
        priceText.clearText()
    }

    private companion object {
        const val BATCH_TIMES_MIN = 2
        const val BATCH_TIMES_MAX = 30
    }
}