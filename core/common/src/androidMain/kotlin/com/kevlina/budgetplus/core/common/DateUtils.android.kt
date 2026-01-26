package com.kevlina.budgetplus.core.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

actual val LocalDate.shortFormatted: String
    get() = toJavaLocalDate().format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault())
    )

actual val LocalDate.mediumFormatted: String
    get() = toJavaLocalDate().format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
    )

actual val LocalDate.fullFormatted: String
    get() = toJavaLocalDate().format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.getDefault())
    )