package com.kevlina.budgetplus.core.data.fixtures

import androidx.annotation.RestrictTo
import com.kevlina.budgetplus.core.common.Currency
import com.kevlina.budgetplus.core.data.CurrencyExchangeRepo
import com.kevlina.budgetplus.core.data.remote.Record
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

@RestrictTo(RestrictTo.Scope.TESTS)
class FakeCurrencyExchangeRepo(
    override var preferredCurrencyCode: String = "USD",
    preferredCurrencySymbol: String? = null,
    /**
     * The rate applied when converting from the preferred currency into the book's currency.
     * A null value simulates an unresolved exchange rate.
     */
    var bookCurrencyRate: Double? = 1.0,
) : CurrencyExchangeRepo {

    override val exchangeRateChange = MutableStateFlow(Unit)

    override val preferredCurrencySymbol: Flow<String?> =
        flowOf(preferredCurrencySymbol ?: preferredCurrencyCode)

    override val displayInPreferredCurrency: StateFlow<Boolean>
        field = MutableStateFlow(true)

    override fun updatePreferredCurrency(currency: Currency) {
        preferredCurrencyCode = currency.currencyCode
    }

    override fun formatPreferredCurrency(price: Double, alwaysShowSymbol: Boolean): String {
        return "$price $preferredCurrencyCode"
    }

    override fun formatBookCurrency(price: Double, alwaysShowSymbol: Boolean): String {
        return "$price"
    }

    override fun convertToBookCurrency(price: Double, fromCurrencyCode: String): Double? {
        return bookCurrencyRate?.let { price * it }
    }

    override fun getDisplayPrice(record: Record): Double {
        if (!displayInPreferredCurrency.value) return record.price
        val rate = bookCurrencyRate ?: return record.price

        val preferredPrice = record.preferredPrice
        return if (
            preferredPrice != null &&
            preferredCurrencyCode.equals(record.preferredCurrencyCode, ignoreCase = true)
        ) {
            preferredPrice
        } else {
            record.price / rate
        }
    }

    override fun formatDisplayPrice(price: Double, alwaysShowSymbol: Boolean): String {
        return if (displayInPreferredCurrency.value && bookCurrencyRate != null) {
            "$price $preferredCurrencyCode"
        } else {
            "$price"
        }
    }

    override fun toggleDisplayInPreferredCurrency() {
        displayInPreferredCurrency.value = !displayInPreferredCurrency.value
    }
}
