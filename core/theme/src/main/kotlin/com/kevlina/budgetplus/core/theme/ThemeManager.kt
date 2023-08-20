package com.kevlina.budgetplus.core.theme

import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    preferenceHolder: PreferenceHolder,
) {

    private var colorToneCache by preferenceHolder.bindObject(ColorTone.MilkTea)

    private val _colorTone = MutableStateFlow(colorToneCache)
    val colorTone: StateFlow<ColorTone> = _colorTone.asStateFlow()

    fun setColorTone(colorTone: ColorTone) {
        colorToneCache = colorTone
        _colorTone.value = colorTone
    }
}