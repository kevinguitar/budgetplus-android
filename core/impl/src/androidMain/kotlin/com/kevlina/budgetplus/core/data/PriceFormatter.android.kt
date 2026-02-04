package com.kevlina.budgetplus.core.data

import co.touchlab.kermit.Logger
import com.kevlina.budgetplus.core.common.plainPriceString
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

actual fun getCurrencySymbol(currencyCode: String?): String {
    val code = currencyCode ?: fallbackCurrencyCode
    return try {
        Currency.getInstance(code).getSymbol(Locale.getDefault())
    } catch (e: Exception) {
        Logger.e(e) { "Failed to get currency symbol for code: $code" }
        code
    }
}

actual fun formatPrice(price: Double, currencyCode: String?, alwaysShowSymbol: Boolean): String {
    val currency = try {
        Currency.getInstance(currencyCode ?: fallbackCurrencyCode)
    } catch (_: Exception) {
        null
    }

    return if (currency != null && (alwaysShowSymbol || currency.getSymbol(Locale.getDefault()).length == 1)) {
        val pricingFormat = NumberFormat.getCurrencyInstance().apply {
            this.currency = currency
            roundingMode = RoundingMode.HALF_UP
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }
        pricingFormat.format(price)
    } else {
        price.plainPriceString
    }
}

actual fun getDefaultCurrencyCode(): String {
    return Currency.getInstance(Locale.getDefault()).currencyCode
}
