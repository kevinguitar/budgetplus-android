package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.ads.AdsBanner
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AppTheme

@Composable
fun OverviewContent(
    navigator: Navigator,
    isHideAds: Boolean,
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(AppTheme.maxContentWidth)
    ) {

        Box(modifier = Modifier.weight(1F)) {

            val listState = rememberLazyListState()
            val isSearchVisible by remember(listState) {
                derivedStateOf {
                    !listState.isScrollInProgress
                }
            }

            OverviewList(
                navigator = navigator,
                listState = listState,
                header = {
                    OverviewHeader(
                        navigator = navigator,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
            )

            SearchButton(
                navigator = navigator,
                isVisible = isSearchVisible
            )
        }

        if (!isHideAds) {
            AdsBanner()
        }
    }
}