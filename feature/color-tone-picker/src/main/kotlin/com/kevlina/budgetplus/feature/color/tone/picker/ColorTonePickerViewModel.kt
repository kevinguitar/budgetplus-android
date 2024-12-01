package com.kevlina.budgetplus.feature.color.tone.picker

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.theme.ColorTone
import com.kevlina.budgetplus.core.theme.ThemeColorSemantic
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.theme.ThemeManager
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class ColorTonePickerViewModel @Inject constructor(
    authManager: AuthManager,
    preferenceHolder: PreferenceHolder,
    private val themeManager: ThemeManager,
    private val bubbleRepo: BubbleRepo,
    private val stringProvider: StringProvider,
    private val snackbarSender: SnackbarSender,
    private val tracker: Tracker,
) : ViewModel() {

    val isPremium = authManager.isPremium

    val previewColors = themeManager.previewColors
    var previousProcessedLink: String? = null

    private val currentColorTone get() = themeManager.colorTone.value

    private val _selectedColorTone = MutableStateFlow(currentColorTone)
    val selectedColorTone: StateFlow<ColorTone> = _selectedColorTone.asStateFlow()

    private var isShareColorsBubbleShown by preferenceHolder.bindBoolean(false)

    val isSaveEnabled = combine(
        selectedColorTone,
        themeManager.hasEditedCustomTheme
    ) { selectedTone, hasEditedCustomTheme ->
        selectedTone != currentColorTone || hasEditedCustomTheme
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun getThemeColors(colorTone: ColorTone): ThemeColors {
        return themeManager.getThemeColors(colorTone)
    }

    fun selectColorTone(colorTone: ColorTone) {
        _selectedColorTone.value = colorTone
    }

    fun setColorTone(colorTone: ColorTone) {
        themeManager.setColorTone(colorTone)
    }

    fun setPreviewColors(previewColors: ThemeColors) {
        themeManager.setPreviewColors(previewColors)
    }

    fun onColorPicked(semantic: ThemeColorSemantic, colorHexCode: String) {
        themeManager.onColorPicked(semantic, colorHexCode)
    }

    fun shareMyColors(context: Context) {
        val colorsLink = themeManager.generateCustomColorsLink()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, stringProvider[R.string.menu_share_colors, colorsLink])
        }
        context.startActivity(Intent.createChooser(intent, stringProvider[R.string.cta_share]))
    }

    fun trackUnlockPremium() {
        tracker.logEvent("color_tone_unlock_premium")
    }

    fun highlightShareButton(dest: BubbleDest) {
        if (!isShareColorsBubbleShown) {
            isShareColorsBubbleShown = true
            bubbleRepo.addBubbleToQueue(dest)
        }
    }

    fun processHexFromLink(hexFromLink: String) {
        previousProcessedLink = hexFromLink
        if (themeManager.processHexFromLink(hexFromLink)) {
            snackbarSender.send(R.string.color_tone_applied_from_link)
        } else {
            snackbarSender.send(R.string.fallback_error_message)
        }
    }
}