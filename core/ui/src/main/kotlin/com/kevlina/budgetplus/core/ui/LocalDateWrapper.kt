package com.kevlina.budgetplus.core.ui

import androidx.compose.runtime.Immutable
import java.time.LocalDate

// java.time.LocalDate isn't stable, create a wrapper class to make it stable.
@Immutable
@JvmInline
value class LocalDateWrapper(val value: LocalDate)

fun LocalDate.wrapped() = LocalDateWrapper(this)