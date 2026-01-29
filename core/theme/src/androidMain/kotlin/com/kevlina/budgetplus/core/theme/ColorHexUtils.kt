package com.kevlina.budgetplus.core.theme

import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.Locale
import androidx.compose.ui.graphics.Color as ComposeColor

fun CharSequence.convertHexToColor(): Int {
    return Color.parseColor("#$this")
}

fun ComposeColor.toHexCode(): String {
    val argb = toArgb()
    val r = Color.red(argb)
    val g = Color.green(argb)
    val b = Color.blue(argb)
    return String.format(Locale.getDefault(), "%02X%02X%02X", r, g, b)
}