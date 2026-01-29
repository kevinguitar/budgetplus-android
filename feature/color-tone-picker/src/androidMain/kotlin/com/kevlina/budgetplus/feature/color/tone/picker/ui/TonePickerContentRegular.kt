package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.ColorTone
import com.kevlina.budgetplus.core.theme.ThemeColorSemantic
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.containerPadding
import com.kevlina.budgetplus.core.ui.thenIf

private val MinColorCarouselHeight = 200.dp

@Composable
internal fun TonePickerContentRegular(
    selectedColorTone: ColorTone,
    pagerState: PagerState,
    isPremium: Boolean,
    getThemeColors: (ColorTone) -> ThemeColors,
    unlockPremium: () -> Unit,
    onColorPicked: (ThemeColorSemantic, String) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .containerPadding()
    ) {

        BoxWithConstraints(
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
        ) {

            var previewSize by remember { mutableStateOf(IntSize.Zero) }

            val previewHeight = with(LocalDensity.current) { previewSize.height.toDp() }
            val needScroll = maxHeight - previewHeight < MinColorCarouselHeight

            Column(
                modifier = Modifier.thenIf(needScroll) {
                    Modifier.verticalScroll(rememberScrollState())
                }
            ) {

                TonePreview(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp)
                        .onPlaced { previewSize = it.size }
                )

                ColorToneCarousel(
                    selectedColorTone = selectedColorTone,
                    pagerState = pagerState,
                    isPremium = isPremium,
                    getThemeColors = getThemeColors,
                    unlockPremium = unlockPremium,
                    onColorPicked = onColorPicked,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                        .thenIf(needScroll) { Modifier.height(MinColorCarouselHeight) }
                        .thenIf(!needScroll) { Modifier.weight(1F) }
                )
            }
        }
    }
}