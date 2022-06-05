package com.kevingt.moneybook.utils

import java.math.RoundingMode

val Double.priceText: String
    get() = toBigDecimal()
        .setScale(2, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()

val Double.dollar: String
    get() = "$$priceText"

val Double.roundUpPrice: Double
    get() = toBigDecimal()
        .setScale(2, RoundingMode.HALF_UP)
        .toDouble()