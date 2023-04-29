package com.kevlina.budgetplus.feature.add.record

import com.google.common.truth.Truth.assertThat
import com.kevlina.budgetplus.feature.add.record.ui.CalculatorAction
import com.kevlina.budgetplus.feature.add.record.ui.CalculatorButton
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class CalculatorViewModelTest {

    @Before
    fun setup() {
        calculator.clearPrice()
    }

    @Test
    fun `clearing the pricing`() {
        val statement = "36"
        input(statement)
        assertThat(calculator.priceText.value).isEqualTo(statement)

        calculator.clearPrice()
        assertThat(calculator.priceText.value).isEqualTo(CalculatorViewModel.EMPTY_PRICE)
    }

    @Test
    fun `evaluating the result`() {
        input("3×6")
        assertThat(calculator.needEvaluate.value).isTrue()

        evaluate()
        assertThat(calculator.needEvaluate.value).isFalse()
        assertThat(calculator.priceText.value).isEqualTo("18")
    }

    @Test
    fun `complicated statement should be evaluated correctly`() {
        input("2.54+1.65×64.2÷9.01")
        evaluate()
        assertThat(calculator.priceText.value).isEqualTo("14.3")
    }

    @Test
    fun `operator should be replaced correctly`() {
        input("3+-1×+÷2")
        assertThat(calculator.priceText.value).isEqualTo("3-1÷2")
    }

    @Test
    fun `duplicated dot should be omitted`() {
        input("1.5.4+2..1...2")
        assertThat(calculator.priceText.value).isEqualTo("1.54+2.12")
    }

    private val calculator = CalculatorViewModel(vibrator = null, toaster = null)
    private val buttons = CalculatorButton.values()

    private fun evaluate() {
        calculator.onCalculatorAction(mockk(), CalculatorAction.Ok)
    }

    private fun input(statement: String) {
        statement.toCharArray().forEach { char ->
            val btn = buttons.first { it.text == char }
            calculator.onInput(btn)
        }
    }
}