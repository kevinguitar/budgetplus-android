package com.kevlina.budgetplus.core.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun CharSequence.convertHexToColorInt(): Int {
    val hex = if (startsWith("#")) substring(1) else toString()
    return when (hex.length) {
        6 -> (0xFF000000 or hex.toLong(16)).toInt()
        8 -> hex.toLong(16).toInt()
        else -> throw IllegalArgumentException("Invalid hex color: $this")
    }
}

internal fun CharSequence.convertHexToColor(): Color {
    return Color(convertHexToColorInt())
}

internal fun Color.toHexCode(): String {
    val argb = toArgb()
    val r = (argb shr 16) and 0xFF
    val g = (argb shr 8) and 0xFF
    val b = argb and 0xFF
    return buildString {
        append(r.toHexDigit())
        append(g.toHexDigit())
        append(b.toHexDigit())
    }
}

private fun Int.toHexDigit(): String {
    return toString(16).uppercase().padStart(2, '0')
}