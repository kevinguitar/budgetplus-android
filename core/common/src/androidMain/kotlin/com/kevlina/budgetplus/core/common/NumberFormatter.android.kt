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