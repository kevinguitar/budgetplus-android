package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.ui.graphics.Color

@Suppress("MagicNumber")
val chartColors = listOf(
    Color(0xFFEBD8CC),
    Color(0xFFCCC8AD),
    Color(0xFFCCCCCC),
    Color(0xFFEAD3BE),
    Color(0xFFF4DCCE),
    Color(0xFFF0E5D5),
    Color(0xFFF1E3A7),
)

/**
 *  For PieChart to avoid color collision when drawing back to the origin.
 */
val chartColorsStaggered = chartColors +
    chartColors.reversed().drop(2) +
    chartColors.drop(2)