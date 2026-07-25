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
     * Formats [bookPrice] (an amount expressed in the book's currency) for the given [display].
     *
     * @param bookPrice The amount expressed in the book's currency.
     * @param display The currency to present [bookPrice] in. See [CurrencyDisplay].
     * @return The formatted price string, or null when it cannot be meaningfully presented:
     *  - [CurrencyDisplay.Book]: when the book's currency matches the preferred currency (no
     *    conversion is meaningful) or the book's currency is unknown.
     *  - [CurrencyDisplay.Preferred]: when the exchange rate is not resolved.
     *  - [CurrencyDisplay.Selected]: never null, always follows the current toggle.
     */
    fun formatCurrency(
        bookPrice: Double,
        display: CurrencyDisplay,
        alwaysShowSymbol: Boolean = false,
    ): String?

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
     * Resolves the price of the [record] in the given [display] currency.
     *
     * When resolving in the preferred currency and the record was originally created in that
     * currency, the recorded [Record.preferredPrice] is used directly instead of converting the
     * book's currency price back, which would result in a doubled conversion.
     *
     * @param display The currency to resolve the price in. See [CurrencyDisplay].
     * @return The price expressed in the [display] currency, matching what [formatDisplayPrice]
     *  formats with.
     */
    fun getDisplayPrice(record: Record, display: CurrencyDisplay = CurrencyDisplay.Selected): Double

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

/**
 * Identifies which currency a price should be presented in.
 */
enum class CurrencyDisplay {

    /** The book's own currency. */
    Book,

    /** The user's preferred currency. */
    Preferred,

    /**
     * Whichever currency the user currently has selected via the display toggle
     * (see [CurrencyExchangeRepo.displayInPreferredCurrency]).
     */
    Selected,
}