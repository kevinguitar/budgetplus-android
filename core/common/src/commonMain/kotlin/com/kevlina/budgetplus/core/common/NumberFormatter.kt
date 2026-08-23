package com.kevlina.budgetplus.core.common

expect val Double.roundUpRatioText: String

expect fun CharSequence.parseToPrice(): Double

expect val Double.plainPriceString: String

/**
 * Formats the integer [value] with the locale-aware grouping separators, e.g. 1000 -> "1,000"
 * for en-US and "1.000" for de-DE.
 */
expect fun formatGroupedInteger(value: Long): String