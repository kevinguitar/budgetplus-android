package com.kevlina.budgetplus.feature.color.tone.picker

import androidx.annotation.FloatRange
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.blend

internal fun ThemeColors.blend(
    target: ThemeColors,
    @FloatRange(from = 0.0, to = 1.0) ratio: Float,
): ThemeColors {
    return ThemeColors(
        light = light.blend(target.light, ratio),
        lightBg = lightBg.blend(target.lightBg, ratio),
        primary = primary.blend(target.primary, ratio),
        dark = dark.blend(target.dark, ratio),
    )
}