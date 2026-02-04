package com.kevlina.budgetplus.core.data

expect fun getCurrencySymbol(currencyCode: String?): String

expect fun formatPrice(price: Double, currencyCode: String?, alwaysShowSymbol: Boolean): String

expect fun getDefaultCurrencyCode(): String

/**
 * For backward compatibility before we introduced currency code support
 */
val fallbackCurrencyCode get() = "TWD"