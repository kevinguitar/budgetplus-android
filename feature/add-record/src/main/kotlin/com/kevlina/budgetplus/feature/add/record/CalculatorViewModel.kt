package com.kevlina.budgetplus.feature.add.record

import android.content.Context
import androidx.compose.runtime.Stable
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.common.plainPriceString
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.VibratorManager
import com.kevlina.budgetplus.feature.add.record.ui.CalculatorAction
import com.kevlina.budgetplus.feature.add.record.ui.CalculatorButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.objecthunter.exp4j.ExpressionBuilder
import timber.log.Timber
import java.math.RoundingMode
import javax.inject.Inject

@Stable
class CalculatorViewModel @Inject constructor(
    private val vibrator: VibratorManager?,
    private val toaster: Toaster?,
) {

    private val _priceText = MutableStateFlow(EMPTY_PRICE)
    val priceText: StateFlow<String> = _priceText.asStateFlow()

    private val _price = MutableStateFlow(0.0)
    val price: StateFlow<Double> = _price.asStateFlow()

    val needEvaluate: StateFlow<Boolean> = priceText.mapState { text ->
        text.any { it in operatorChars }
    }

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
        vibrator?.vibrate()

        val currentText = priceText.value
        when (btn) {
            CalculatorButton.Back -> delete()

            // Replace the latest operator
            CalculatorButton.Plus, CalculatorButton.Minus,
            CalculatorButton.Multiply, CalculatorButton.Divide,
            -> when {
                currentText == EMPTY_PRICE -> Unit
                currentText.last() in operatorChars -> {
                    _priceText.value = currentText.dropLast(1) + btn.text
                }

                else -> appendText(btn)
            }

            // Do not allow multiple dots in the same number
            CalculatorButton.Dot -> when {
                currentText.any { it in operatorChars } -> {
                    val lastNumber = currentText.split(*operatorChars).last()
                    if (!lastNumber.contains(CalculatorButton.Dot.text)) {
                        appendText(btn)
                    }
                }

                currentText.contains(CalculatorButton.Dot.text) -> Unit
                else -> appendText(btn)
            }

            else -> if (currentText == EMPTY_PRICE) {
                _priceText.value = btn.text.toString()
            } else {
                appendText(btn)
            }
        }
    }

    private fun appendText(btn: CalculatorButton) {
        _priceText.value = priceText.value + btn.text
    }

    private fun evaluate() {
        val text = priceText.value
            .replace(CalculatorButton.Multiply.text, '*')
            .replace(CalculatorButton.Divide.text, '/')

        val rawResult: Double = try {
            val expression = ExpressionBuilder(text).build()
            val validation = expression.validate()
            if (!validation.isValid) {
                toaster?.showMessage(validation.errors.joinToString())
                Timber.e("Calculator validation error. Raw: $text")
                return
            }

            expression.evaluate()
        } catch (e: Exception) {
            toaster?.showError(e)
            Timber.e("Calculator evaluation error. Raw: $text")
            return
        }

        _priceText.value = rawResult.plainPriceString
        _price.value = rawResult.toBigDecimal()
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    fun clearPrice() {
        _price.value = 0.0
        _priceText.value = EMPTY_PRICE
    }

    fun onCalculatorAction(context: Context, action: CalculatorAction) {
        vibrator?.vibrate()
        when {
            action == CalculatorAction.Clear -> clearPrice()
            needEvaluate.value -> evaluate()
            else -> {
                evaluate()
                _recordFlow.sendEvent(context)
            }
        }
    }

    private fun delete() {
        val currentPrice = priceText.value
        when {
            currentPrice.length == 1 -> _priceText.value = EMPTY_PRICE
            currentPrice.isNotEmpty() -> _priceText.value = currentPrice.dropLast(1)
        }
    }

    companion object {
        const val EMPTY_PRICE: String = "0"
    }
}