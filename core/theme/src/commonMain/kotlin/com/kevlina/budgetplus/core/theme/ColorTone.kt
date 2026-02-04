package com.kevlina.budgetplus.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.color_tone_barbie
import budgetplus.core.common.generated.resources.color_tone_countryside
import budgetplus.core.common.generated.resources.color_tone_customized
import budgetplus.core.common.generated.resources.color_tone_dusk
import budgetplus.core.common.generated.resources.color_tone_lavender
import budgetplus.core.common.generated.resources.color_tone_milk_tea
import budgetplus.core.common.generated.resources.color_tone_nemo_sea
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

@Serializable
enum class ColorTone {
    MilkTea, Dusk, Countryside, Barbie, Lavender, NemoSea, Customized;

    val nameRes: StringResource
        get() = when (this) {
            MilkTea -> Res.string.color_tone_milk_tea
            Dusk -> Res.string.color_tone_dusk
            Countryside -> Res.string.color_tone_countryside
            Barbie -> Res.string.color_tone_barbie
            Lavender -> Res.string.color_tone_lavender
            NemoSea -> Res.string.color_tone_nemo_sea
            Customized -> Res.string.color_tone_customized
        }

    val requiresPremium: Boolean
        get() = when (this) {
            MilkTea, Dusk, Countryside -> false
            Barbie, Lavender, NemoSea, Customized -> true
        }
}

enum class ThemeColorSemantic {
    Light, LightBg, Primary, Dark
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

        val Dusk = ThemeColors(
            light = Color(0xFFFFFFFF),
            lightBg = Color(0xFFFCF9E3),
            primary = Color(0xFFFFAD8F),
            dark = Color(0xFF949494),
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