package com.kevlina.budgetplus.utils

import java.math.RoundingMode
import java.text.NumberFormat

private val pricingFormat by lazy {
    NumberFormat.getNumberInstance()
        .also {
            it.roundingMode = RoundingMode.HALF_UP
            it.minimumFractionDigits = 0
            it.maximumFractionDigits = 2
        }
}

private val ratioFormat by lazy {
    NumberFormat.getNumberInstance()
        .also {
            it.roundingMode = RoundingMode.HALF_UP
            it.minimumFractionDigits = 0
            it.maximumFractionDigits = 1
        }
}

val Double.roundUpPriceText: String
    get() = pricingFormat.format(this)

val Double.roundUpPercentageText: String
    get() = ratioFormat.format(this)

val Double.dollar: String
    get() = "$$roundUpPriceText"

val String.parseToPrice: Double
    get() = requireNotNull(pricingFormat.parse(this)) {
        "Incorrect price format: $this"
    }.toDouble()