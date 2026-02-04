package com.kevlina.budgetplus.core.ui

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun Color.darken(@FloatRange(from = 0.0, to = 1.0) factor: Float): Color {
    val hsl = rgbToHsl(red, green, blue)
    // Lower the saturation and the lightness
    val h = hsl[0]
    val s = hsl[1] * factor
    val l = hsl[2] * factor
    return hslToColor(h, s, l, alpha)
}

fun Color.blend(
    target: Color,
    @FloatRange(from = 0.0, to = 1.0)
    ratio: Float,
): Color {
    return lerp(this, target, ratio)
}

private fun rgbToHsl(r: Float, g: Float, b: Float): FloatArray {
    val max = max(r, max(g, b))
    val min = min(r, min(g, b))
    val delta = max - min

    var h = 0f
    var s = 0f
    val l = (max + min) / 2f

    if (max != min) {
        s = if (l < 0.5f) delta / (max + min) else delta / (2f - max - min)

        h = when (max) {
            r -> (g - b) / delta + (if (g < b) 6f else 0f)
            g -> (b - r) / delta + 2f
            else -> (r - g) / delta + 4f
        }
        h /= 6f
    }

    return floatArrayOf(h * 360f, s, l)
}

private fun hslToColor(h: Float, s: Float, l: Float, a: Float): Color {
    val c = (1f - abs(2f * l - 1f)) * s
    val m = l - 0.5f * c
    val x = c * (1f - abs((h / 60f) % 2f - 1f))

    val (r, g, b) = when {
        h < 60f -> Triple(c, x, 0f)
        h < 120f -> Triple(x, c, 0f)
        h < 180f -> Triple(0f, c, x)
        h < 240f -> Triple(0f, x, c)
        h < 300f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(r + m, g + m, b + m, a)
}