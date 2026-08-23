package com.kevlina.budgetplus.feature.add.record

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import com.kevlina.budgetplus.core.common.AppCoroutineScope
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.ExpressionEvaluator
import com.kevlina.budgetplus.core.common.Logger
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.VibratorManager
import com.kevlina.budgetplus.core.common.formatGroupedInteger
import com.kevlina.budgetplus.core.common.plainPriceString
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.settings.api.CalculatorButtonType
import com.kevlina.budgetplus.core.settings.api.CalculatorSettings
import com.kevlina.budgetplus.feature.add.record.ui.CalculatorAction
import com.kevlina.budgetplus.feature.add.record.ui.CalculatorButton
import com.kevlina.budgetplus.feature.freeze.FreezeBookViewModel
import com.kevlina.budgetplus.feature.speak.record.SpeakToRecordViewModel
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Inject
class CalculatorViewModel(
    val vibrator: VibratorManager,
    val calculatorSettings: CalculatorSettings,
    val speakToRecordVm: SpeakToRecordViewModel,
    val freezeBookVm: FreezeBookViewModel,
    private val snackbarSender: SnackbarSender,
    private val expressionEvaluator: ExpressionEvaluator,
    @AppCoroutineScope private val appScope: CoroutineScope,
) {
    val priceText = TextFieldState(EMPTY_PRICE)

    /**
     * The un-formatted expression that backs [priceText]. Numbers here never contain the
     * grouping separator and always use '.' as the decimal point, so it can be parsed and
     * evaluated safely. [priceText] only holds its locale-formatted rendering for display.
     */
    private var rawText: String = EMPTY_PRICE

    val needEvaluate: Flow<Boolean> = snapshotFlow { priceText.text }
        .map { text -> text.any { it in operatorChars } }
        .distinctUntilChanged()

    val recordFlow: EventFlow<Unit>
        field = MutableEventFlow<Unit>()

    private val operatorChars = listOf(
        CalculatorButton.Plus,
        CalculatorButton.Minus,
        CalculatorButton.Multiply,
        CalculatorButton.Divide
    )
        .map { it.text }
        .toCharArray()

    fun onInput(btn: CalculatorButton) {
        val currentText = rawText
        when (btn) {
            CalculatorButton.Delete -> delete()

            // Replace the latest operator
            CalculatorButton.Plus, CalculatorButton.Minus,
            CalculatorButton.Multiply, CalculatorButton.Divide,
                -> when {
                currentText == EMPTY_PRICE -> Unit
                currentText.last() in operatorChars -> {
                    setRawText(currentText.dropLast(1) + btn.text)
                }

                else -> appendText(btn)
            }

            // Do not allow multiple dots in the same number
            CalculatorButton.Dot -> when {
                // The '.' button acts as a "00" quick-input when double-zero mode is enabled.
                calculatorSettings.buttonType.value == CalculatorButtonType.DoubleZero -> when {
                    currentText == EMPTY_PRICE -> Unit
                    currentText.last() in operatorChars -> Unit
                    else -> setRawText(currentText + CalculatorButtonType.DoubleZero.text)
                }

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
                setRawText(btn.text.toString())
            } else {
                appendText(btn)
            }
        }
    }

    private fun appendText(btn: CalculatorButton) {
        setRawText(rawText + btn.text)
    }

    /**
     * Updates the raw backing expression and renders its locale-formatted counterpart into
     * [priceText] for display.
     */
    private fun setRawText(raw: String) {
        rawText = raw
        priceText.setTextAndPlaceCursorAtEnd(getPriceText(raw))
    }

    /**
     * Renders [raw] expression into a locale-formatted, display-ready string. Each number segment
     * gets grouping separators applied to its integer part (e.g. "1000+2000" -> "1,000+2,000"),
     * while operators and in-progress decimals are preserved as typed.
     */
    private fun getPriceText(raw: String): String {
        val builder = StringBuilder()
        val number = StringBuilder()

        fun flushNumber() {
            if (number.isEmpty()) return
            builder.append(formatNumberSegment(number.toString()))
            number.clear()
        }

        for (char in raw) {
            if (char in operatorChars) {
                flushNumber()
                builder.append(char)
            } else {
                number.append(char)
            }
        }
        flushNumber()
        return builder.toString()
    }

    /** Applies grouping separators to the integer part of a single number segment. */
    private fun formatNumberSegment(segment: String): String {
        val dotIndex = segment.indexOf(CalculatorButton.Dot.text)
        val integerPart = if (dotIndex == -1) segment else segment.substring(0, dotIndex)
        // Keep the fractional part (and a trailing dot) verbatim so mid-typing input isn't altered.
        val fractionPart = if (dotIndex == -1) "" else segment.substring(dotIndex)

        val groupedInteger = integerPart.toLongOrNull()
            ?.let { formatGroupedInteger(it) }
            ?: integerPart
        return groupedInteger + fractionPart
    }

    private fun evaluate() {
        val text = rawText
            .replace(CalculatorButton.Multiply.text, '*')
            .replace(CalculatorButton.Divide.text, '/')

        when (val result = expressionEvaluator.evaluate(text)) {
            is ExpressionEvaluator.Result.Success -> setPrice(result.value)
            is ExpressionEvaluator.Result.Error -> {
                appScope.launch { snackbarSender.send(result.message) }
                Logger.e(CalculatorException(), "Validation error. Raw: $text")
            }
        }
    }

    fun setPrice(priceNumber: Double) {
        setRawText(priceNumber.plainPriceString)
    }

    fun clearPrice() {
        setRawText(EMPTY_PRICE)
    }

    fun onCalculatorAction(action: CalculatorAction) {
        when (action) {
            CalculatorAction.Clear -> clearPrice()
            CalculatorAction.Evaluate -> evaluate()
            CalculatorAction.Ok -> {
                evaluate()
                recordFlow.sendEvent()
            }
        }
    }

    private fun delete() {
        setRawText(if (rawText.length <= 1) EMPTY_PRICE else rawText.dropLast(1))
    }

    companion object {
        const val EMPTY_PRICE: String = "0"
    }
}

internal class CalculatorException : RuntimeException("Calculation error")