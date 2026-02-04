package com.kevlina.budgetplus.core.common

import java.math.RoundingMode
import java.text.NumberFormat

private val ratioFormat by lazy {
    NumberFormat.getNumberInstance()
        .also {
            it.roundingMode = RoundingMode.HALF_UP
            it.minimumFractionDigits = 0
            it.maximumFractionDigits = 1
        }
}

actual val Double.roundUpRatioText: String
    get() = ratioFormat.format(this)

private val priceFormat: NumberFormat
    get() = NumberFormat.getNumberInstance()
        .also {
            it.roundingMode = RoundingMode.HALF_UP
            it.minimumFractionDigits = 0
            it.maximumFractionDigits = 2
        }

actual fun CharSequence.parseToPrice(): Double {
    return requireNotNull(priceFormat.parse(toString())) {
        "Incorrect price format: $this"
    }.toDouble()
}

actual val Double.plainPriceString: String
    get() = toBigDecimal()
        .setScale(2, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()