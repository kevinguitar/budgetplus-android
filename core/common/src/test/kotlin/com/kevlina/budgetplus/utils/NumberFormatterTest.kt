package com.kevlina.budgetplus.utils

import com.google.common.truth.Truth.assertThat
import com.kevlina.budgetplus.core.common.plainPriceString
import com.kevlina.budgetplus.core.common.roundUpPercentageText
import com.kevlina.budgetplus.core.common.roundUpPriceText
import org.junit.Test

class NumberFormatterTest {

    @Test
    fun `WHEN a double is rounded to a price THEN round to 2nd decimal place`() {
        assertThat(1.123.roundUpPriceText).isEqualTo("1.12")
        assertThat(1.001.roundUpPriceText).isEqualTo("1")
        assertThat(1.456.roundUpPriceText).isEqualTo("1.46")
        assertThat(1.999.roundUpPriceText).isEqualTo("2")
    }

    @Test
    fun `WHEN a big number is rounded to a price THEN show the comma on thousand places`() {
        assertThat(1000.0.roundUpPriceText).isEqualTo("1,000")
        assertThat(1000.49.roundUpPriceText).isEqualTo("1,000.49")
        assertThat(1000000.0.roundUpPriceText).isEqualTo("1,000,000")
        assertThat(1000000.001.roundUpPriceText).isEqualTo("1,000,000")
        assertThat(1000000000.0.roundUpPriceText).isEqualTo("1,000,000,000")
        assertThat(1000000000.789.roundUpPriceText).isEqualTo("1,000,000,000.79")
    }

    @Test
    fun `WHEN a number is rounded to ratio THEN round to 1st decimal place`() {
        assertThat(23.123.roundUpPercentageText).isEqualTo("23.1")
        assertThat(65.001.roundUpPercentageText).isEqualTo("65")
        assertThat(45.456.roundUpPercentageText).isEqualTo("45.5")
        assertThat(87.999.roundUpPercentageText).isEqualTo("88")
    }

    @Test
    fun `plainPriceString extension is working correctly`() {
        assertThat(12.0.plainPriceString).isEqualTo("12")
        assertThat(12.356.plainPriceString).isEqualTo("12.36")
        assertThat(12.349.plainPriceString).isEqualTo("12.35")
        assertThat(12.99.plainPriceString).isEqualTo("12.99")
        assertThat(12.999.plainPriceString).isEqualTo("13")
    }
}