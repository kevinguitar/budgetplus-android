package com.kevlina.budgetplus.book.record.vm

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.kevlina.budgetplus.book.record.CalculatorAction
import com.kevlina.budgetplus.book.record.CalculatorButton
import com.kevlina.budgetplus.utils.roundUpPriceText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.RoundingMode
import javax.inject.Inject

class CalculatorViewModel @Inject constructor(
    private val vibrator: Vibrator?
) {

    private val _priceText = MutableStateFlow(EMPTY_PRICE)
    val priceText: StateFlow<String> = _priceText.asStateFlow()

    private val _price = MutableStateFlow(0.0)
    val price: StateFlow<Double> = _price.asStateFlow()

    fun onInput(btn: CalculatorButton) {
        vibrate()

        val currentPrice = priceText.value
        when (btn) {
            CalculatorButton.Plus, CalculatorButton.Minus,
            CalculatorButton.Multiply, CalculatorButton.Divide -> {
                if (currentPrice != EMPTY_PRICE) {
                    _priceText.value = currentPrice + btn.text
                }
            }
            CalculatorButton.DoubleZero -> if (currentPrice != EMPTY_PRICE) {
                _priceText.value = currentPrice + btn.text
            }
            else -> _priceText.value = if (currentPrice == EMPTY_PRICE) {
                btn.text
            } else {
                currentPrice + btn.text
            }
        }
    }

    fun onAction(action: CalculatorAction) {
        vibrate()

        when (action) {
            CalculatorAction.Clear -> clearPrice()
            CalculatorAction.Equal -> evaluate()
            CalculatorAction.Delete -> delete()
        }
    }

    fun evaluate() {
        val text = priceText.value
            .replace('×', '*')
            .replace('÷', '/')

        val expression = ExpressionBuilder(text).build()
        if (!expression.validate().isValid) {
            return
        }

        val rawResult: Double = expression.evaluate()

        _priceText.value = rawResult.roundUpPriceText
        _price.value = rawResult.toBigDecimal()
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    fun clearPrice() {
        _price.value = 0.0
        _priceText.value = EMPTY_PRICE
    }

    private fun delete() {
        val currentPrice = priceText.value
        when {
            currentPrice.length == 1 -> _priceText.value = EMPTY_PRICE
            currentPrice.isNotEmpty() -> _priceText.value = currentPrice.dropLast(1)
        }
    }

    private val vibrationDuration get() = 2L

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(vibrationDuration)
        }
    }

    companion object {
        private const val EMPTY_PRICE: String = "0"
    }
}