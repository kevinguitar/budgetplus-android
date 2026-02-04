package com.kevlina.budgetplus.core.common

expect val Double.roundUpRatioText: String

expect fun CharSequence.parseToPrice(): Double

expect val Double.plainPriceString: String