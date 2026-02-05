package com.kevlina.budgetplus.core.common

expect fun getCurrencySymbol(currencyCode: String?): String

expect fun formatPriceWithCurrency(price: Double, currencyCode: String?, alwaysShowSymbol: Boolean): String

expect fun getDefaultCurrencyCode(): String

expect fun getAvailableCurrencies(): List<Currency>

data class Currency(
    val name: String,
    val currencyCode: String,
    val symbol: String,
)

/**
 * For backward compatibility before we introduced currency code support
 */
val fallbackCurrencyCode get() = "TWD"