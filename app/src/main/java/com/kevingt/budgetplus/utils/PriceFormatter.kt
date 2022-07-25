package com.kevingt.budgetplus.utils

import java.math.RoundingMode

val Double.roundUpPriceText: String
    get() = toBigDecimal()
        .setScale(2, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()

val Double.roundUpPercentageText: String
    get() = toBigDecimal()
        .setScale(1, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()

val Double.dollar: String
    get() = "$$roundUpPriceText"

val Double.roundUpPrice: Double
    get() = toBigDecimal()
        .setScale(2, RoundingMode.HALF_UP)
        .toDouble()