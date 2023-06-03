package com.kevlina.budgetplus.core.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

val LocalDate.shortFormatted: String
    get() = format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault()))

val LocalDate.mediumFormatted: String
    get() = format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()))

val LocalDate.withCurrentTime: Long
    get() = LocalDateTime.of(this, LocalTime.now()).toEpochSecond(ZoneOffset.UTC)