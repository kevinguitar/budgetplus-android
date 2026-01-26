package com.kevlina.budgetplus.core.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSCalendar
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterFullStyle
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSDateFormatterStyle
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual val LocalDate.shortFormatted: String
    get() = format(NSDateFormatterShortStyle)

actual val LocalDate.mediumFormatted: String
    get() = format(NSDateFormatterMediumStyle)

actual val LocalDate.fullFormatted: String
    get() = format(NSDateFormatterFullStyle)

private fun LocalDate.format(style: NSDateFormatterStyle): String {
    val formatter = NSDateFormatter()
    formatter.dateStyle = style
    formatter.timeStyle = NSDateFormatterNoStyle
    formatter.locale = NSLocale.currentLocale

    val components = toNSDateComponents()
    val date = NSCalendar.currentCalendar.dateFromComponents(components) ?: return ""
    return formatter.stringFromDate(date)
}