package com.kevlina.budgetplus.core.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

val LocalDate.shortFormatted: String
    get() = format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))

val LocalDate.withCurrentTime: Long
    get() = LocalDateTime.of(this, LocalTime.now()).toEpochSecond(ZoneOffset.UTC)