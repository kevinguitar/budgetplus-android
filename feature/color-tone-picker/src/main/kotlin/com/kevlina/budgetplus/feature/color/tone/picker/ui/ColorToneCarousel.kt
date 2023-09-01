package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.ColorTone
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.PagerIndicator
import com.kevlina.budgetplus.core.ui.lerp
import kotlin.math.abs

val colorTones = ColorTone.entries.toList()

@Composable
internal fun ColorToneCarousel(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.weight(1F)
        ) { page ->

            ColorToneCard(
                colorTone = colorTones[page],
                modifier = Modifier.graphicsLayer {
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
        pagerState = rememberPagerState { 1 },
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    )
}
