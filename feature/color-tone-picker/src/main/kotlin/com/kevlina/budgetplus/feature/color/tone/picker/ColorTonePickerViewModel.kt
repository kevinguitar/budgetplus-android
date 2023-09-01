package com.kevlina.budgetplus.feature.color.tone.picker

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.theme.ColorTone
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.theme.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ColorTonePickerViewModel @Inject constructor(
    authManager: AuthManager,
    private val themeManager: ThemeManager,
) : ViewModel() {

    val isPremium = authManager.isPremium

    val colorTone = themeManager.colorTone

    fun setColorTone(colorTone: ColorTone) {
        themeManager.setColorTone(colorTone)
    }

    fun setPreviewColors(previewColors: ThemeColors) {
        themeManager.setPreviewColors(previewColors)
    }
}