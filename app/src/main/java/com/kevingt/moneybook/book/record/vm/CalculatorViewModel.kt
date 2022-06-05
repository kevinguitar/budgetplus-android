package com.kevingt.moneybook.book.record.vm

import com.kevingt.moneybook.book.record.CalculatorButton
import com.kevingt.moneybook.utils.priceText
import com.kevingt.moneybook.utils.roundUpPrice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.objecthunter.exp4j.ExpressionBuilder
import javax.inject.Inject

class CalculatorViewModel @Inject constructor() {

    private val _priceText = MutableStateFlow(EMPTY_PRICE)
    val priceText: StateFlow<String> = _priceText.asStateFlow()

    private val _price = MutableStateFlow(0.0)
    val price: StateFlow<Double> = _price.asStateFlow()

    fun onInput(btn: CalculatorButton) {
        when (btn) {
            CalculatorButton.Equal -> evaluate()
            CalculatorButton.Plus, CalculatorButton.Minus,
            CalculatorButton.Multiply, CalculatorButton.Divide -> {
                if (priceText.value != EMPTY_PRICE) {
                    _priceText.value = priceText.value + btn.text
                }
            }
            else -> {
                val currentPrice = priceText.value
                _priceText.value = if (currentPrice == EMPTY_PRICE) {
                    btn.text
                } else {
                    currentPrice + btn.text
                }
            }
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

        _price.value = rawResult.roundUpPrice
        _priceText.value = rawResult.priceText
    }

    fun clearPrice() {
        _price.value = 0.0
        _priceText.value = EMPTY_PRICE
    }

    companion object {
        private const val EMPTY_PRICE: String = "0"
    }
}