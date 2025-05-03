package com.kevlina.budgetplus.feature.currency.picker

import androidx.compose.runtime.Immutable

@Immutable
data class CurrencyState(
    val name: String,
    val currencyCode: String,
    val symbol: String,
    val isSelected: Boolean,
)