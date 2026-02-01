package com.kevlina.budgetplus.feature.records

import kotlinx.serialization.Serializable

@Serializable
enum class RecordsSortMode {
    Date, Price
}