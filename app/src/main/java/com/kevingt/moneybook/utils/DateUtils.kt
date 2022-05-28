package com.kevingt.moneybook.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

val LocalDate.formatted: String
    get() = format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))