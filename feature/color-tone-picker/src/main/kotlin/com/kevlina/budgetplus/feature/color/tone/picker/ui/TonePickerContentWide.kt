package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.ads.AdsBanner
import com.kevlina.budgetplus.core.theme.ColorTone

@Composable
internal fun TonePickerContentWide(
    selectedColorTone: ColorTone,
    pagerState: PagerState,
    isPremium: Boolean,
    unlockPremium: () -> Unit,
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {

        Column(modifier = Modifier.weight(1F)) {

            TonePreview(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp)
            )

            if (!isPremium) {
                AdsBanner()
            }
        }

        ColorToneCarousel(
            selectedColorTone = selectedColorTone,
            pagerState = pagerState,
            isPremium = isPremium,
            unlockPremium = unlockPremium,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
                .padding(vertical = 16.dp)
        )
    }
}