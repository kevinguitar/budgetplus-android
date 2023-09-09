package com.kevlina.budgetplus.core.theme

import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    preferenceHolder: PreferenceHolder,
    private val authManager: AuthManager,
    private val tracker: Tracker,
) {

    private var colorToneCache by preferenceHolder.bindObject(ColorTone.MilkTea)

    private val _colorTone = MutableStateFlow(colorToneCache)
    val colorTone: StateFlow<ColorTone> = _colorTone.asStateFlow()

    private val _previewColors = MutableStateFlow<ThemeColors?>(null)
    val previewColors: StateFlow<ThemeColors?> = _previewColors.asStateFlow()

    fun setColorTone(colorTone: ColorTone) {
        if (!authManager.isPremium.value && colorTone.requiresPremium) {
            Timber.e("ThemeManager: Attempting to apply a premium theme for a free user. $colorTone")
            return
        }

        colorToneCache = colorTone
        _colorTone.value = colorTone
        //TODO: track color selection!
    }

    fun setPreviewColors(previewColors: ThemeColors) {
        _previewColors.value = previewColors
    }

    fun clearPreviewColors() {
        _previewColors.value = null
    }
}