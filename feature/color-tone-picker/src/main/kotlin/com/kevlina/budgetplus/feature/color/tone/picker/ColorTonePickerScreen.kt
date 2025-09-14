package com.kevlina.budgetplus.feature.color.tone.picker

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.theme.ColorTone
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.feature.color.tone.picker.ui.TonePickerContentRegular
import com.kevlina.budgetplus.feature.color.tone.picker.ui.TonePickerContentWide
import com.kevlina.budgetplus.feature.color.tone.picker.ui.colorTones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlin.math.abs
import kotlin.math.roundToInt

private const val COLOR_BLEND_DEBOUNCE = 200
private const val CUSTOM_COLOR_TRANSITION = 100L

@Composable
fun ColorTonePickerScreen(
    navController: NavController,
    hexFromLink: String?,
) {
    val vm = hiltViewModel<ColorTonePickerViewModel>()

    val isPremium by vm.isPremium.collectAsStateWithLifecycle()
    val selectedColorTone by vm.selectedColorTone.collectAsStateWithLifecycle()
    val previewColors by vm.previewColors.collectAsStateWithLifecycle()
    val isSaveEnabled by vm.isSaveEnabled.collectAsStateWithLifecycle()

    var isExitDialogShown by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(
        initialPage = colorTones.indexOf(selectedColorTone),
        pageCount = { colorTones.size }
    )

    LaunchedEffect(hexFromLink) {
        if (hexFromLink != null && vm.previousProcessedLink != hexFromLink) {
            pagerState.animateScrollToPage(colorTones.indexOf(ColorTone.Customized))
            // Apply a tiny delay to showcase the color transition.
            delay(CUSTOM_COLOR_TRANSITION)
            vm.processHexFromLink(hexFromLink)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPageOffsetFraction }
            // Debounce to avoid excessive recomposition.
            .distinctUntilChangedBy { (it * COLOR_BLEND_DEBOUNCE).roundToInt() }
            .onEach { fraction ->
                val currentIndex = pagerState.currentPage
                val currentColors = vm.getThemeColors(colorTones[currentIndex])
                val colors = if (fraction == 0F) {
                    currentColors
                } else {
                    val targetIndex = (if (fraction > 0) currentIndex + 1 else currentIndex - 1)
                        .coerceIn(0, colorTones.lastIndex)
                    val targetColors = vm.getThemeColors(colorTones[targetIndex])
                    currentColors.blend(target = targetColors, ratio = abs(fraction))
                }
                vm.setPreviewColors(colors)
            }
            .flowOn(Dispatchers.Default)
            .collect()
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            vm.selectColorTone(colorTones[page])
        }
    }

    fun navigateUp() {
        if (isSaveEnabled) {
            isExitDialogShown = true
        } else {
            navController.navigateUp()
        }
    }

    fun unlockPremium() {
        vm.trackUnlockPremium()
        navController.navigate(AddDest.UnlockPremium)
    }

    if (isSaveEnabled) {
        BackHandler(onBack = ::navigateUp)
    }

    // Override the app-level local colors with the state of color tone picker.
    CompositionLocalProvider(
        LocalAppColors provides (previewColors ?: vm.getThemeColors(selectedColorTone)),
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalAppColors.current.light)
        ) {

            TopBar(
                title = stringResource(id = R.string.color_tone_picker_title),
                navigateUp = ::navigateUp,
                menuActions = {
                    if (selectedColorTone == ColorTone.Customized) {
                        MenuAction(
                            imageVector = Icons.Rounded.Share,
                            description = stringResource(id = R.string.cta_share),
                            onClick = vm::shareMyColors,
                            modifier = Modifier.onPlaced {
                                vm.highlightShareButton(
                                    BubbleDest.ColorsSharing(
                                        size = it.size,
                                        offset = it.positionInRoot()
                                    )
                                )
                            },
                        )
                    }

                    MenuAction(
                        imageVector = Icons.Rounded.Check,
                        description = stringResource(id = R.string.cta_save),
                        enabled = isSaveEnabled,
                        onClick = {
                            if (!selectedColorTone.requiresPremium || isPremium) {
                                vm.setColorTone(selectedColorTone)
                                navController.navigateUp()
                            } else {
                                unlockPremium()
                            }
                        }
                    )
                }
            )

            AdaptiveScreen(
                modifier = Modifier.weight(1F),
                regularContent = {
                    TonePickerContentRegular(
                        selectedColorTone = selectedColorTone,
                        pagerState = pagerState,
                        isPremium = isPremium,
                        getThemeColors = vm::getThemeColors,
                        unlockPremium = ::unlockPremium,
                        onColorPicked = vm::onColorPicked
                    )
                },
                wideContent = {
                    TonePickerContentWide(
                        selectedColorTone = selectedColorTone,
                        pagerState = pagerState,
                        isPremium = isPremium,
                        getThemeColors = vm::getThemeColors,
                        unlockPremium = ::unlockPremium,
                        onColorPicked = vm::onColorPicked
                    )
                }
            )
        }

        if (isExitDialogShown) {
            ConfirmDialog(
                message = stringResource(id = R.string.unsaved_warning_message),
                onConfirm = {
                    navController.navigateUp()
                    isExitDialogShown = false
                },
                onDismiss = { isExitDialogShown = false }
            )
        }
    }
}