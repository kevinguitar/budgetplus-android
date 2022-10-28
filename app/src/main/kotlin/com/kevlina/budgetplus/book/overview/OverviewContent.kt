package com.kevlina.budgetplus.book.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.monetize.AdsBanner

@Composable
fun OverviewContent(
    navigator: Navigator,
    balance: Double,
    isHideAds: Boolean,
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(AppTheme.maxContentWidth)
    ) {

        Box(modifier = Modifier.weight(1F)) {

            OverviewList(
                navigator = navigator,
                header = { OverviewHeader(modifier = Modifier.padding(horizontal = 16.dp)) },
            )

            BalanceFloatingLabel(
                balance = balance,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.BottomCenter)
            )
        }

        if (!isHideAds) {
            AdsBanner()
        }
    }
}