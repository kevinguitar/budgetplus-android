package com.kevlina.budgetplus.core.common

import kotlinx.datetime.LocalDate

expect val LocalDate.shortFormatted: String

expect val LocalDate.mediumFormatted: String

expect val LocalDate.fullFormatted: String