package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.ads.AdsBanner
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme

@Composable
internal fun OverviewContent(
    uiState: OverviewContentUiState,
    navigator: Navigator,
    isHideAds: Boolean,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(AppTheme.maxContentWidth)
    ) {

        OverviewList(
            uiState = uiState.listUiState,
            navigator = navigator,
            header = {
                OverviewHeader(
                    uiState = uiState.headerUiState,
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

@Immutable
internal class OverviewContentUiState(
    val headerUiState: OverviewHeaderUiState,
    val listUiState: OverviewListUiState,
) {
    companion object {
        val preview = OverviewContentUiState(
            headerUiState = OverviewHeaderUiState.preview,
            listUiState = OverviewListUiState.preview
        )
    }
}

@Preview
@Composable
private fun OverviewContent_Preview() = AppTheme {
    OverviewContent(
        uiState = OverviewContentUiState.preview,
        navigator = Navigator.empty,
        isHideAds = true,
        modifier = Modifier.background(LocalAppColors.current.light)
    )
}