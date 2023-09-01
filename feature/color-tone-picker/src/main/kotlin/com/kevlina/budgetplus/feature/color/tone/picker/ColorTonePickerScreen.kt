package com.kevlina.budgetplus.feature.color.tone.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.feature.color.tone.picker.ui.TonePickerContent
import com.kevlina.budgetplus.feature.color.tone.picker.ui.colorTones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlin.math.abs
import kotlin.math.roundToInt

private const val COLOR_BLEND_DEBOUNCE = 200

@Composable
fun ColorTonePickerScreen(
    navigator: Navigator,
) {

    val vm = hiltViewModel<ColorTonePickerViewModel>()

    val isPremium by vm.isPremium.collectAsStateWithLifecycle()
    val currentColorTone by vm.colorTone.collectAsStateWithLifecycle()

    var selectedColorTone by remember { mutableStateOf(currentColorTone) }
    var isExitDialogShown by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(
        initialPage = colorTones.indexOf(currentColorTone),
        pageCount = { colorTones.size }
    )

    val previewColors by remember(pagerState) {
        snapshotFlow { pagerState.currentPageOffsetFraction }
            // Debounce to avoid excessive recomposition.
            .distinctUntilChangedBy { (it * COLOR_BLEND_DEBOUNCE).roundToInt() }
            .map { fraction ->
                val currentIndex = pagerState.currentPage
                val currentColors = colorTones[currentIndex].themeColors
                if (fraction == 0F) {
                    return@map currentColors
                }

                val targetIndex = (if (fraction >= 0) currentIndex + 1 else currentIndex - 1)
                    .coerceIn(0, colorTones.lastIndex)
                val targetColors = colorTones[targetIndex].themeColors
                currentColors.blend(target = targetColors, ratio = abs(fraction))
                    .also(vm::setPreviewColors)
            }
            .flowOn(Dispatchers.Default)
    }.collectAsStateWithLifecycle(initialValue = selectedColorTone.themeColors)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedColorTone = colorTones[page]
        }
    }

    // Override the app-level local colors with the state of color tone picker.
    CompositionLocalProvider(
        LocalAppColors provides previewColors,
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalAppColors.current.light)
        ) {

            TopBar(
                title = stringResource(id = R.string.color_tone_picker_title),
                navigateUp = {
                    if (selectedColorTone != currentColorTone) {
                        isExitDialogShown = true
                    } else {
                        navigator.navigateUp()
                    }
                },
                menuActions = {
                    MenuAction(
                        imageVector = Icons.Rounded.Check,
                        description = stringResource(id = R.string.cta_save),
                        enabled = currentColorTone != selectedColorTone,
                        onClick = {
                            vm.setColorTone(selectedColorTone)
                            navigator.navigateUp()
                        }
                    )
                }
            )

            AdaptiveScreen(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(1F),
                regularContent = {
                    TonePickerContent(
                        pagerState = pagerState,
                        isPremium = isPremium,
                    )
                },
                wideContent = { }
            )
        }

        if (isExitDialogShown) {
            //TODO:
        }
    }
}