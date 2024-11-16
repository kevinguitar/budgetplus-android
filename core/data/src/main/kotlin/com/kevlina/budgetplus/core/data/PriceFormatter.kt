package com.kevlina.budgetplus.core.data

import java.math.RoundingMode
import java.text.NumberFormat

private val numberFormat: NumberFormat
    get() = NumberFormat.getNumberInstance()
        .also {
            it.roundingMode = RoundingMode.HALF_UP
            it.minimumFractionDigits = 0
            it.maximumFractionDigits = 2
        }

fun CharSequence.parseToPrice(): Double {
    return requireNotNull(numberFormat.parse(toString())) {
        "Incorrect price format: $this"
    }.toDouble()
}

val Double.plainPriceString: String
    get() = toBigDecimal()
        .setScale(2, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()