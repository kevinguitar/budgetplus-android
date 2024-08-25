package com.kevlina.budgetplus.feature.add.record

import android.app.Activity
import com.google.common.truth.Truth.assertThat
import com.kevlina.budgetplus.core.common.impl.FakeToaster
import com.kevlina.budgetplus.core.data.impl.FakeVibratorManager
import com.kevlina.budgetplus.feature.add.record.ui.CalculatorAction
import com.kevlina.budgetplus.feature.add.record.ui.CalculatorButton
import io.mockk.mockk
import org.junit.Test

class CalculatorViewModelTest {

    @Test
    fun `clearing the pricing`() {
        val statement = "36"
        calculator.input(statement)
        assertThat(calculator.priceText.value).isEqualTo(statement)

        calculator.clearPrice()
        assertThat(calculator.priceText.value).isEqualTo(CalculatorViewModel.EMPTY_PRICE)
    }

    @Test
    fun `evaluating the result`() {
        calculator.input("3×6")
        assertThat(calculator.needEvaluate.value).isTrue()

        calculator.evaluate()
        assertThat(calculator.needEvaluate.value).isFalse()
        assertThat(calculator.priceText.value).isEqualTo("18")
    }

    @Test
    fun `complicated statement should be evaluated correctly`() {
        calculator.input("2.54+1.65×64.2÷9.01")
        calculator.evaluate()
        assertThat(calculator.priceText.value).isEqualTo("14.3")
    }

    @Test
    fun `operator should be replaced correctly`() {
        calculator.input("3+-1×+÷2")
        assertThat(calculator.priceText.value).isEqualTo("3-1÷2")
    }

    @Test
    fun `duplicated dot should be omitted`() {
        calculator.input("1.5.4+2..1...2")
        assertThat(calculator.priceText.value).isEqualTo("1.54+2.12")
    }

    @Test
    fun `delete button should work correctly`() {
        calculator.input("123")
        calculator.onInput(CalculatorButton.Back)
        calculator.input("+321")
        calculator.onInput(CalculatorButton.Back)
        calculator.evaluate()
        assertThat(calculator.priceText.value).isEqualTo("44")
    }

    private val calculator = CalculatorViewModel(vibrator = FakeVibratorManager(), toaster = FakeToaster())
}

fun CalculatorViewModel.input(statement: String) {
    statement.toCharArray().forEach { char ->
        val btn = CalculatorButton.entries.first { it.text == char }
        onInput(btn)
    }
}

fun CalculatorViewModel.evaluate() {
    onCalculatorAction(mockk<Activity>(), CalculatorAction.Ok)
}
