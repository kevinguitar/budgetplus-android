package com.kevlina.budgetplus.core.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

expect val LocalDate.shortFormatted: String

expect val LocalDate.mediumFormatted: String

expect val LocalDate.fullFormatted: String

val LocalDate.withCurrentTime: Long
    get() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return atTime(now.time).toInstant(TimeZone.UTC).epochSeconds
    }