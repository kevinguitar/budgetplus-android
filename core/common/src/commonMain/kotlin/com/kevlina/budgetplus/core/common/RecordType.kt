package com.kevlina.budgetplus.core.common

import kotlinx.serialization.Serializable

@Serializable
enum class RecordType {
    Expense, Income
}