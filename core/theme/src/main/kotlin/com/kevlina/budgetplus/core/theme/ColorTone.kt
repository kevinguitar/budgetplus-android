package com.kevlina.budgetplus.core.theme

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.kevlina.budgetplus.core.common.R

enum class ColorTone {
    MilkTea, Countryside, Barbie, Lavender, NemoSea, TaiwanLv;

    val themeColors: ThemeColors
        get() = when (this) {
            MilkTea -> ThemeColors.MilkTea
            Countryside -> ThemeColors.Countryside
            Barbie -> ThemeColors.Barbie
            Lavender -> ThemeColors.Lavender
            NemoSea -> ThemeColors.NemoSea
            TaiwanLv -> ThemeColors.TaiwanLv
        }


    val nameRes: Int
        @StringRes get() = when (this) {
            MilkTea -> R.string.color_tone_milk_tea
            Countryside -> R.string.color_tone_countryside
            Barbie -> R.string.color_tone_barbie
            Lavender -> R.string.color_tone_lavender
            NemoSea -> R.string.color_tone_nemo_sea
            TaiwanLv -> R.string.color_tone_taiwan_lv
        }

    val requiresPremium: Boolean
        get() = when (this) {
            MilkTea, Countryside -> false
            Barbie, Lavender, NemoSea, TaiwanLv -> true
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

        val Countryside = ThemeColors(
            light = Color(0xFFfff5f8),
            lightBg = Color(0xFFE4E4D0),
            primary = Color(0xFF94A684),
            dark = Color(0xFFb9985a)
        )

        val Barbie = ThemeColors(
            light = Color(0xFFf9fafb),
            lightBg = Color(0xFFf9e7ef),
            primary = Color(0xFFE59EBF),
            dark = Color(0xFF849FAF)
        )

        val Lavender = ThemeColors(
            light = Color(0xFFfdfbf7),
            lightBg = Color(0xFFf5edd6),
            primary = Color(0xFFc6a4ea),
            dark = Color(0xFF9081d0)
        )

        val NemoSea = ThemeColors(
            light = Color(0xFFFFFFFF),
            lightBg = Color(0xFFe3f5fc),
            primary = Color(0xFF61c7f0),
            dark = Color(0xFFEC7C1E)
        )

        val TaiwanLv = ThemeColors(
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