package com.kevingt.moneybook.book.record.vm

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.kevingt.moneybook.book.record.CalculatorAction
import com.kevingt.moneybook.book.record.CalculatorButton
import com.kevingt.moneybook.utils.priceText
import com.kevingt.moneybook.utils.roundUpPrice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.objecthunter.exp4j.ExpressionBuilder
import javax.inject.Inject

class CalculatorViewModel @Inject constructor(
    private val vibrator: Vibrator?
) {

    private val _priceText = MutableStateFlow(EMPTY_PRICE)
    val priceText: StateFlow<String> = _priceText.asStateFlow()

    private val _price = MutableStateFlow(0.0)
    val price: StateFlow<Double> = _price.asStateFlow()

    fun onInput(btn: CalculatorButton) {
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
        vibrate()
    }

    fun onAction(action: CalculatorAction) {
        when (action) {
            CalculatorAction.Clear -> clearPrice()
            CalculatorAction.Equal -> evaluate()
            CalculatorAction.Delete -> delete()
        }
        vibrate()
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

        _price.value = rawResult.roundUpPrice
        _priceText.value = rawResult.priceText
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    vibrationDuration,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(vibrationDuration)
        }
    }

    companion object {
        private const val EMPTY_PRICE: String = "0"
    }
}