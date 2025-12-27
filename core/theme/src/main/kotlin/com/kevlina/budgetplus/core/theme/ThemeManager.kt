package com.kevlina.budgetplus.core.theme

import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.bundle
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.NAV_COLORS_PATH
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

@SingleIn(AppScope::class)
@Inject
class ThemeManager(
    preferenceHolder: PreferenceHolder,
    private val authManager: AuthManager,
    private val tracker: Tracker,
) {

    private var colorToneCache by preferenceHolder.bindObject(ColorTone.MilkTea)
    private var customizedColorsCache by preferenceHolder.bindString(ThemeColors.MilkTea.encode())

    // It represents the current theme colors in the customized theme.
    private var currentCustomColors = decodeThemeColors(customizedColorsCache) ?: ThemeColors.MilkTea

    // This field isn't saved to the preference, it represents the colors that the user is currently mixing.
    private var editedCustomColors = currentCustomColors

    val colorTone: StateFlow<ColorTone>
        field = MutableStateFlow(colorToneCache)

    val themeColors: StateFlow<ThemeColors>
        field = MutableStateFlow<ThemeColors>(getThemeColors(colorToneCache))

    val previewColors: StateFlow<ThemeColors?>
        field = MutableStateFlow<ThemeColors?>(null)

    val hasEditedCustomTheme: StateFlow<Boolean>
        field = MutableStateFlow(false)

    fun getThemeColors(colorTone: ColorTone): ThemeColors = when (colorTone) {
        ColorTone.MilkTea -> ThemeColors.MilkTea
        ColorTone.Dusk -> ThemeColors.Dusk
        ColorTone.Countryside -> ThemeColors.Countryside
        ColorTone.Barbie -> ThemeColors.Barbie
        ColorTone.Lavender -> ThemeColors.Lavender
        ColorTone.NemoSea -> ThemeColors.NemoSea
        ColorTone.Customized -> editedCustomColors
    }

    fun setColorTone(newColorTone: ColorTone) {
        if (!authManager.isPremium.value && newColorTone.requiresPremium) {
            Timber.e("ThemeManager: Attempting to apply a premium theme for a free user. $colorTone")
            return
        }

        if (newColorTone == ColorTone.Customized) {
            saveCustomColors(editedCustomColors)
        }

        colorToneCache = newColorTone
        colorTone.value = newColorTone
        themeColors.value = getThemeColors(newColorTone)

        tracker.logEvent("color_tone_changed", bundle {
            putString("tone", colorTone.toString())
        })
    }

    fun setPreviewColors(newPreviewColors: ThemeColors) {
        previewColors.value = newPreviewColors
    }

    fun clearPreviewColors() {
        previewColors.value = null
        hasEditedCustomTheme.value = false
        // Reset the edited custom colors
        editedCustomColors = currentCustomColors
    }

    fun onColorPicked(semantic: ThemeColorSemantic, colorHexCode: String) {
        val newColor = try {
            Color(colorHexCode.convertHexToColor())
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse the picked color $colorHexCode")
            return
        }

        val newColors = when (semantic) {
            ThemeColorSemantic.Light -> editedCustomColors.copy(light = newColor)
            ThemeColorSemantic.LightBg -> editedCustomColors.copy(lightBg = newColor)
            ThemeColorSemantic.Primary -> editedCustomColors.copy(primary = newColor)
            ThemeColorSemantic.Dark -> editedCustomColors.copy(dark = newColor)
        }

        setPreviewColors(newColors)
        editedCustomColors = newColors
        tracker.logEvent("color_tone_custom_color_picked")

        // If the customized tone is not selected, write the new color hex directly.
        if (colorTone.value == ColorTone.Customized) {
            hasEditedCustomTheme.value = currentCustomColors != newColors
        } else {
            saveCustomColors(newColors)
        }
    }

    fun generateCustomColorsLink(): String {
        val colorsLink = APP_DEEPLINK.toUri()
            .buildUpon()
            .appendPath(NAV_COLORS_PATH)
            // Should match BookDest.Colors
            .appendQueryParameter("hex", editedCustomColors.encode())
            .build()
        tracker.logEvent("color_tone_link_generated")
        return colorsLink.toString().lowercase()
    }

    /**
     *  @return if the hex is decoded successfully.
     */
    fun processHexFromLink(hexFromLink: String): Boolean {
        val sharedColors = decodeThemeColors(hexFromLink) ?: return false
        setPreviewColors(sharedColors)
        editedCustomColors = sharedColors
        hasEditedCustomTheme.value = currentCustomColors != sharedColors
        tracker.logEvent("share_colors_link_opened")
        return true
    }

    private fun saveCustomColors(newColors: ThemeColors) {
        customizedColorsCache = newColors.encode()
        currentCustomColors = newColors
        editedCustomColors = newColors
        hasEditedCustomTheme.value = false
    }

    private fun ThemeColors.encode(): String {
        return listOf(light, lightBg, primary, dark)
            .joinToString(COLOR_HEX_CONCAT_CHAR) { it.toHexCode() }
    }

    @Suppress("MagicNumber")
    private fun decodeThemeColors(value: String): ThemeColors? {
        val hexCodes = value.split(COLOR_HEX_CONCAT_CHAR)
        return try {
            ThemeColors(
                light = Color(hexCodes[0].convertHexToColor()),
                lightBg = Color(hexCodes[1].convertHexToColor()),
                primary = Color(hexCodes[2].convertHexToColor()),
                dark = Color(hexCodes[3].convertHexToColor()),
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to decode the theme colors. raw value=$value")
            null
        }
    }

    companion object {
        private const val COLOR_HEX_CONCAT_CHAR = ";"
    }
}