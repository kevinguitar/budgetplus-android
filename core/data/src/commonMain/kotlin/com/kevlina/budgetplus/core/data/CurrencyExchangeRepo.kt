package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.common.Currency
import com.kevlina.budgetplus.core.data.remote.Record
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CurrencyExchangeRepo {

    val exchangeRateChange: Flow<Unit>

    /**
     * @return The symbol for the preferred currency.
     */
    val preferredCurrencySymbol: Flow<String?>

    /**
     * @return The currency code that the user prefers to use.
     */
    val preferredCurrencyCode: String

    /**
     * @return Whether to display prices in the preferred currency or book's currency.
     */
    val displayInPreferredCurrency: StateFlow<Boolean>

    /**
     * Updates the currency that the user prefers to use.
     */
    fun updatePreferredCurrency(currency: Currency)

    /**
     * Formats the given price using the preferred currency.
     * @return Formatted price string, or null if the preferred currency rate is not resolved.
     */
    fun formatPreferredCurrency(price: Double, alwaysShowSymbol: Boolean = false): String?

    /**
     * Formats the given book's currency price.
     * @return Formatted price string, or null if the book's currency matches the preferred currency
     *  (i.e. no conversion is meaningful) or the book's currency is unknown.
     */
    fun formatBookCurrency(price: Double, alwaysShowSymbol: Boolean = false): String?

    /**
     * Converts a price expressed in [fromCurrencyCode] into the book's currency.
     *
     * @param price The amount expressed in [fromCurrencyCode].
     * @param fromCurrencyCode The currency code the [price] is expressed in, defaults to the
     *  preferred currency.
     * @return The converted amount in the book's currency, or null if the rate is not resolved.
     */
    fun convertToBookCurrency(
        price: Double,
        fromCurrencyCode: String = preferredCurrencyCode,
    ): Double?

    /**
     * Resolves the price of the [record] in the currently displayed currency.
     *
     * When displaying in the preferred currency and the record was originally created in that
     * currency, the recorded [Record.preferredPrice] is used directly instead of converting the
     * book's currency price back, which would result in a doubled conversion.
     *
     * @return The price expressed in the currency that [formatDisplayPrice] formats with.
     */
    fun getDisplayPrice(record: Record): Double

    /**
     * Formats a price that is already expressed in the currently displayed currency,
     * e.g. a price resolved via [getDisplayPrice], or a sum of such prices.
     */
    fun formatDisplayPrice(price: Double, alwaysShowSymbol: Boolean = false): String

    /**
     * Formats the price of the [record] in the currently displayed currency, respecting the
     * currency the record was originally created in.
     */
    fun formatRecordPrice(record: Record, alwaysShowSymbol: Boolean = false): String =
        formatDisplayPrice(getDisplayPrice(record), alwaysShowSymbol)

    /**
     * Toggle whether to display prices in the preferred currency or book's currency.
     */
    fun toggleDisplayInPreferredCurrency()
}