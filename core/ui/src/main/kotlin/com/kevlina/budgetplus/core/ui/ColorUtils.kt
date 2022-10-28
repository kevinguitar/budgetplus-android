package com.kevlina.budgetplus.core.ui

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color

fun Color.lighten(@FloatRange(from = 1.0) factor: Float): Color {
    return copy(
        red = (red * factor).coerceAtMost(1F),
        green = (green * factor).coerceAtMost(1F),
        blue = (blue * factor).coerceAtMost(1F),
    )
}

fun Color.darken(@FloatRange(from = 1.0) factor: Float): Color {
    return copy(
        red = (red * (2 - factor)).coerceAtLeast(0F),
        green = (green * (2 - factor)).coerceAtLeast(0F),
        blue = (blue * (2 - factor)).coerceAtLeast(0F),
    )
}