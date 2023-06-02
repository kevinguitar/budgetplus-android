package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.ads.AdsBanner
import com.kevlina.budgetplus.core.common.nav.Navigator

@Composable
fun OverviewContentWide(
    navigator: Navigator,
    isHideAds: Boolean,
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        ) {

            OverviewHeader(
                navigator = navigator,
                modifier = Modifier
                    .weight(1F)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            )

            if (!isHideAds) {
                AdsBanner()
            }
        }

        val listState = rememberLazyListState()
        val isSearchVisible by remember(listState) {
            derivedStateOf {
                !listState.isScrollInProgress
            }
        }

        Box(modifier = Modifier.weight(1F)) {

            OverviewList(
                navigator = navigator,
                listState = listState
            )

            SearchButton(
                navigator = navigator,
                isVisible = isSearchVisible
            )
        }
    }
}