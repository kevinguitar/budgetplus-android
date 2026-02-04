package com.kevlina.budgetplus.core.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

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

actual val LocalDateTime.shortFormatted: String
    get() = toJavaLocalDateTime().format(
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
    )