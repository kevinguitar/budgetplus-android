package com.kevlina.budgetplus.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ColorTone {
    MilkTea, Blue, Green, Pink, Lavender, Modern;

    val themeColors: ThemeColors
        get() = when (this) {
            MilkTea -> ThemeColors.MilkTea
            Blue -> ThemeColors.Blue
            Green -> ThemeColors.Green
            Pink -> ThemeColors.Pink
            Lavender -> ThemeColors.Lavender
            Modern -> ThemeColors.Modern
        }

    val requiresPremium: Boolean
        get() = when (this) {
            MilkTea -> false
            else -> true
        }
}

@Immutable
data class ThemeColors(
    val light: Color,
    val lightBg: Color,
    val primary: Color,
    val dark: Color,
) {

    companion object {

        val MilkTea = ThemeColors(
            light = Color(0xFFFFF3E0),
            lightBg = Color(0xFFF2E2CD),
            primary = Color(0xFFC1A185),
            dark = Color(0xFF7E8072)
        )

        val Blue = ThemeColors(
            light = Color(0xFFFFFFFF),
            lightBg = Color(0xFFe3f5fc),
            primary = Color(0xFF61c7f0),
            dark = Color(0xFFFD841F)
        )

        val Green = ThemeColors(
            light = Color(0xFFfff5f8),
            lightBg = Color(0xFFE4E4D0),
            primary = Color(0xFF94A684),
            dark = Color(0xFFb9985a)
        )

        val Pink = ThemeColors(
            light = Color(0xFFfff5f2),
            lightBg = Color(0xFFffe2db),
            primary = Color(0xFFf4a9a7),
            dark = Color(0xFF969175)
        )

        val Lavender = ThemeColors(
            light = Color(0xFFfff9f2),
            lightBg = Color(0xFFf5e2f6),
            primary = Color(0xFFd2b6ee),
            dark = Color(0xFF9384D1)
        )

        val Modern = ThemeColors(
            light = Color(0xFFFDFFFD),
            lightBg = Color(0xFFd1ede4),
            primary = Color(0xFF65B4E6),
            dark = Color(0xFFE34F6E)
        )
    }
}

val LocalAppColors = staticCompositionLocalOf {
    ThemeColors(
        light = Color.Unspecified,
        lightBg = Color.Unspecified,
        primary = Color.Unspecified,
        dark = Color.Unspecified,
    )
}