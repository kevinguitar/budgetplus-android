package com.kevlina.budgetplus.core.data

import androidx.compose.runtime.Immutable

@Immutable
data class BatchFrequency(
    val duration: Int,
    val unit: BatchUnit,
)

enum class BatchUnit {
    Month, Week, Day
}