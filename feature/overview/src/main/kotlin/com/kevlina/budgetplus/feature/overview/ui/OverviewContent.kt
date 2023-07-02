package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.ads.AdsBanner
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.feature.overview.OverviewViewModel

@Composable
internal fun OverviewContent(
    vm: OverviewViewModel,
    navigator: Navigator,
    isHideAds: Boolean,
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(AppTheme.maxContentWidth)
    ) {

        OverviewList(
            vm = vm,
            navigator = navigator,
            header = {
                OverviewHeader(
                    vm = vm,
                    navigator = navigator,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            },
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
        )

        if (!isHideAds) {
            AdsBanner()
        }
    }
}