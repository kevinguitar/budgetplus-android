package com.kevlina.budgetplus.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(content: @Composable () -> Unit) {

    val customColors = CustomColors(
        light = Color(0xFFFFF3E0),
        primaryLight = Color(0xFFE0CCB1),
        primary = Color(0xFFC1A185),
        primarySemiDark = Color(0xFFB0836B),
        primaryDark = Color(0xFF907258),
        dark = Color(0xFF7E8072)
    )

    CompositionLocalProvider(
        LocalAppColors provides customColors,
        content = content
    )
}

@Immutable
data class CustomColors(
    val light: Color,
    val primaryLight: Color,
    val primary: Color,
    val primarySemiDark: Color,
    val primaryDark: Color,
    val dark: Color,
)

val LocalAppColors = staticCompositionLocalOf {
    CustomColors(
        light = Color.Unspecified,
        primaryLight = Color.Unspecified,
        primary = Color.Unspecified,
        primarySemiDark = Color.Unspecified,
        primaryDark = Color.Unspecified,
        dark = Color.Unspecified,
    )
}