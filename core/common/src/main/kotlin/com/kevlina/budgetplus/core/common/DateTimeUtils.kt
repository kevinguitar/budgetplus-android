package com.kevlina.budgetplus.core.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

val Long.millisToDateTime: LocalDateTime
    get() = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

val LocalDateTime.shortFormat: String
    get() = format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))