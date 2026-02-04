package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.common.plainPriceString
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.NSNumberFormatterRoundHalfUp
import platform.Foundation.currentLocale

actual fun getCurrencySymbol(currencyCode: String?): String {
    val formatter = NSNumberFormatter().apply {
        this.currencyCode = currencyCode ?: fallbackCurrencyCode
    }
    return formatter.currencySymbol
}

actual fun formatPrice(price: Double, currencyCode: String?, alwaysShowSymbol: Boolean): String {
    val symbol = getCurrencySymbol(currencyCode)

    return if (alwaysShowSymbol || symbol.length == 1) {
        val formatter = NSNumberFormatter().apply {
            this.numberStyle = NSNumberFormatterCurrencyStyle
            this.currencyCode = currencyCode ?: fallbackCurrencyCode
            this.roundingMode = NSNumberFormatterRoundHalfUp
            this.minimumFractionDigits = 0u
            this.maximumFractionDigits = 2u
        }
        formatter.stringFromNumber(NSNumber(price)) ?: price.plainPriceString
    } else {
        price.plainPriceString
    }
}

actual fun getDefaultCurrencyCode(): String {
    return NSLocale.currentLocale.objectForKey(platform.Foundation.NSLocaleCurrencyCode) as? String
        ?: fallbackCurrencyCode
}
