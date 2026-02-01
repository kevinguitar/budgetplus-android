package com.kevlina.budgetplus.feature.overview

import kotlinx.serialization.Serializable

@Serializable
internal enum class OverviewMode {
    AllRecords, GroupByCategories
}