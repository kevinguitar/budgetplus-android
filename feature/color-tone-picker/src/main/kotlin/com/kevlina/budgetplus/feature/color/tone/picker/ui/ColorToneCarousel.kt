package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.lottie.PremiumCrown
import com.kevlina.budgetplus.core.theme.ColorTone
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColorSemantic
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.PagerIndicator
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.darken
import com.kevlina.budgetplus.core.ui.lerp
import kotlin.math.abs

val colorTones = ColorTone.entries.toList()

private const val CARD_TEXT_DARKEN_FACTOR = 0.7F

@Composable
internal fun ColorToneCarousel(
    selectedColorTone: ColorTone,
    pagerState: PagerState,
    isPremium: Boolean,
    getThemeColors: (ColorTone) -> ThemeColors,
    unlockPremium: () -> Unit,
    onColorPicked: (ThemeColorSemantic, String) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            if (selectedColorTone.requiresPremium) {
                PremiumCrown(modifier = Modifier.size(24.dp))
            }

            Text(
                text = stringResource(id = selectedColorTone.nameRes),
                fontSize = FontSize.Large,
                fontWeight = FontWeight.SemiBold,
                color = LocalAppColors.current.primary.darken(CARD_TEXT_DARKEN_FACTOR)
            )
        }

        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.weight(1F)
        ) { page ->

            val colorTone = colorTones[page]

            ColorToneCard(
                colorTone = colorTone,
                themeColors = getThemeColors(colorTone),
                isLocked = colorTone.requiresPremium && !isPremium,
                unlockPremium = unlockPremium,
                onColorPicked = onColorPicked,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = abs(pagerState.calculateOffsetForPage(page))

                        // We animate the alpha and height, between 80% and 100%
                        val lerp = lerp(start = 0.8F, stop = 1F, fraction = 1F - pageOffset)
                        alpha = lerp
                        scaleY = lerp
                    }
            )
        }

        PagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
    }
}

private fun PagerState.calculateOffsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}

@Preview
@Composable
private fun ColorToneCarousel_Preview() = AppTheme {
    ColorToneCarousel(
        selectedColorTone = ColorTone.MilkTea,
        pagerState = rememberPagerState { 1 },
        isPremium = false,
        getThemeColors = { _ -> ThemeColors.MilkTea },
        unlockPremium = {},
        onColorPicked = { _, _ -> },
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    )
}
