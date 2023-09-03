package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.kevlina.budgetplus.core.ads.AdsBanner
import com.kevlina.budgetplus.core.theme.ColorTone
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.thenIf

@Composable
internal fun TonePickerContentRegular(
    selectedColorTone: ColorTone,
    pagerState: PagerState,
    isPremium: Boolean,
    unlockPremium: () -> Unit,
) {

    var previewSize by remember { mutableStateOf(IntSize.Zero) }

    BoxWithConstraints {

        val previewHeight = with(LocalDensity.current) { previewSize.height.toDp() }
        val needScroll = maxHeight - previewHeight <= 100.dp

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(AppTheme.maxContentWidth)
                .thenIf(needScroll) {
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
                unlockPremium = unlockPremium,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .thenIf(needScroll) { Modifier.height(200.dp) }
                    .thenIf(!needScroll) { Modifier.weight(1F) }
            )

            if (!isPremium) {
                AdsBanner()
            }
        }
    }
}