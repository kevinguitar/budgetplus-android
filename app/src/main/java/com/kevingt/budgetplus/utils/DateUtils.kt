package com.kevingt.budgetplus.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

val LocalDate.longFormatted: String
    get() = format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

val LocalDate.shortFormatted: String
    get() = format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))