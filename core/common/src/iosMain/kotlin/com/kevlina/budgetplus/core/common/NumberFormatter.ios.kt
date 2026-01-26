package com.kevlina.budgetplus.core.common

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.NSNumberFormatterRoundHalfUp

private val ratioFormat by lazy {
    NSNumberFormatter().apply {
        minimumFractionDigits = 0u
        maximumFractionDigits = 1u
        roundingMode = NSNumberFormatterRoundHalfUp
        numberStyle = NSNumberFormatterDecimalStyle
    }
}

actual val Double.roundUpRatioText: String
    get() = ratioFormat.stringFromNumber(NSNumber(this)) ?: this.toString()