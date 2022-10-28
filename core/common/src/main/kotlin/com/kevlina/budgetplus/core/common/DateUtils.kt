package com.kevlina.budgetplus.core.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

val LocalDate.shortFormatted: String
    get() = format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))