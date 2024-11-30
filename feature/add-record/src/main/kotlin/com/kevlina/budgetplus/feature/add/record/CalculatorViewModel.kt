package com.kevlina.budgetplus.feature.add.record

import android.content.Context
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.VibratorManager
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.plainPriceString
import com.kevlina.budgetplus.core.ui.SnackbarSender
import com.kevlina.budgetplus.feature.add.record.ui.CalculatorAction
import com.kevlina.budgetplus.feature.add.record.ui.CalculatorButton
import com.kevlina.budgetplus.feature.speak.record.SpeakToRecordViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import net.objecthunter.exp4j.ExpressionBuilder
import timber.log.Timber
import java.math.RoundingMode
import javax.inject.Inject

class CalculatorViewModel @Inject constructor(
    private val vibrator: VibratorManager,
    private val snackbarSender: SnackbarSender,
    val speakToRecordViewModel: SpeakToRecordViewModel,
) {

    val priceText = TextFieldState(EMPTY_PRICE)

    private val _price = MutableStateFlow(0.0)
    val price: StateFlow<Double> = _price.asStateFlow()

    val needEvaluate: Flow<Boolean> = snapshotFlow { priceText.text }
        .map { text -> text.any { it in operatorChars } }
        .distinctUntilChanged()

    private val _recordFlow = MutableEventFlow<Context>()
    val recordFlow: EventFlow<Context> = _recordFlow.asStateFlow()

    private val operatorChars = listOf(
        CalculatorButton.Plus,
        CalculatorButton.Minus,
        CalculatorButton.Multiply,
        CalculatorButton.Divide
    )
        .map { it.text }
        .toCharArray()

    fun onInput(btn: CalculatorButton) {
        vibrator.vibrate()

        val currentText = priceText.text
        when (btn) {
            CalculatorButton.Delete -> delete()

            // Replace the latest operator
            CalculatorButton.Plus, CalculatorButton.Minus,
            CalculatorButton.Multiply, CalculatorButton.Divide,
                -> when {
                currentText == EMPTY_PRICE -> Unit
                currentText.last() in operatorChars -> {
                    priceText.edit {
                        replace(length - 1, length, btn.text.toString())
                    }
                }

                else -> appendText(btn)
            }

            // Do not allow multiple dots in the same number
            CalculatorButton.Dot -> when {
                currentText.any { it in operatorChars } -> {
                    val indexOfLastOp = currentText.indexOfLast { it in operatorChars }
                    val lastNumber = currentText.takeLast(currentText.length - indexOfLastOp - 1)
                    if (!lastNumber.contains(CalculatorButton.Dot.text)) {
                        appendText(btn)
                    }
                }

                currentText.contains(CalculatorButton.Dot.text) -> Unit
                else -> appendText(btn)
            }

            else -> if (currentText == EMPTY_PRICE) {
                priceText.setTextAndPlaceCursorAtEnd(btn.text.toString())
            } else {
                appendText(btn)
            }
        }
    }

    private fun appendText(btn: CalculatorButton) {
        priceText.edit { append(btn.text) }
    }

    private fun evaluate() {
        val text = priceText.text.toString()
            .replace(CalculatorButton.Multiply.text, '*')
            .replace(CalculatorButton.Divide.text, '/')

        val rawResult: Double = try {
            val expression = ExpressionBuilder(text).build()
            val validation = expression.validate()
            if (!validation.isValid) {
                snackbarSender.send(validation.errors.joinToString())
                Timber.e("Calculator validation error. Raw: $text")
                return
            }

            expression.evaluate()
        } catch (e: Exception) {
            snackbarSender.sendError(e)
            Timber.e("Calculator evaluation error. Raw: $text")
            return
        }

        setPrice(rawResult)
    }

    fun setPrice(priceNumber: Double) {
        priceText.setTextAndPlaceCursorAtEnd(priceNumber.plainPriceString)
        _price.value = priceNumber.toBigDecimal()
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    fun clearPrice() {
        _price.value = 0.0
        priceText.setTextAndPlaceCursorAtEnd(EMPTY_PRICE)
    }

    fun onCalculatorAction(context: Context, action: CalculatorAction) {
        vibrator.vibrate()
        when (action) {
            CalculatorAction.Clear -> clearPrice()
            CalculatorAction.Evaluate -> evaluate()
            CalculatorAction.Ok -> {
                evaluate()
                _recordFlow.sendEvent(context)
            }
        }
    }

    private fun delete() {
        priceText.edit {
            when {
                length == 1 -> replace(0, length, EMPTY_PRICE)
                length > 1 -> delete(length - 1, length)
            }
        }
    }

    companion object {
        const val EMPTY_PRICE: String = "0"
    }
}