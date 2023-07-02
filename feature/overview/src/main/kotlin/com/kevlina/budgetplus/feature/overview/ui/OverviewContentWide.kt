package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.ads.AdsBanner
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.feature.overview.OverviewViewModel

@Composable
internal fun OverviewContentWide(
    vm: OverviewViewModel,
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
                vm = vm,
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

        OverviewList(
            vm = vm,
            navigator = navigator,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        )
    }
}