package com.kevlina.budgetplus.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ColorTone {
    MilkTea, Test
}

@Immutable
data class ThemeColors(
    val light: Color,
    val lightBg: Color,
    val primaryLight: Color,
    val primary: Color,
    val primarySemiDark: Color,
    val primaryDark: Color,
    val dark: Color,
) {

    companion object {

        @Suppress("MagicNumber")
        val MilkTea = ThemeColors(
            light = Color(0xFFFFF3E0),
            lightBg = Color(0xFFF2E2CD),
            primaryLight = Color(0xFFE0CCB1),
            primary = Color(0xFFC1A185),
            primarySemiDark = Color(0xFFB0836B),
            primaryDark = Color(0xFF907258),
            dark = Color(0xFF7E8072)
        )

        @Suppress("MagicNumber")
        val Test = ThemeColors(
            light = Color(0xFFFFFFFF),
            lightBg = Color(0xFFFDF2F2),
            primaryLight = Color(0xFFFFDBDB),
            primary = Color(0xFFFFB2B2),
            primarySemiDark = Color(0xFFFDA0A0),
            primaryDark = Color(0xFFFF9797),
            dark = Color(0xFFFF8080)
        )
    }
}

val LocalAppColors = staticCompositionLocalOf {
    ThemeColors(
        light = Color.Unspecified,
        lightBg = Color.Unspecified,
        primaryLight = Color.Unspecified,
        primary = Color.Unspecified,
        primarySemiDark = Color.Unspecified,
        primaryDark = Color.Unspecified,
        dark = Color.Unspecified,
    )
}