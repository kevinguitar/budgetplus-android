package com.kevlina.budgetplus.core.ui

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

fun Color.darken(@FloatRange(from = 0.0, to = 1.0) factor: Float): Color {
    @Suppress("MagicNumber")
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(toArgb(), hsl)
    // Lower the saturation and the lightness
    hsl[1] *= factor
    hsl[2] *= factor
    return Color(ColorUtils.HSLToColor(hsl))
}

fun Color.blend(
    target: Color,
    @FloatRange(from = 0.0, to = 1.0)
    ratio: Float,
): Color {
    return Color(ColorUtils.blendARGB(toArgb(), target.toArgb(), ratio))
}