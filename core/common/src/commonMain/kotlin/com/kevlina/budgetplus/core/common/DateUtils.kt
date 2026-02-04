package com.kevlina.budgetplus.core.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Instant

expect val LocalDate.shortFormatted: String

expect val LocalDate.mediumFormatted: String

expect val LocalDate.fullFormatted: String

expect val LocalDateTime.shortFormatted: String

fun Long.millisToDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
}

fun LocalDate.Companion.now(): LocalDate {
    return Clock.System.todayIn(TimeZone.currentSystemDefault())
}

fun LocalDateTime.Companion.now(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

val LocalDate.withCurrentTime: Long
    get() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return atTime(now.time).toInstant(TimeZone.UTC).epochSeconds
    }