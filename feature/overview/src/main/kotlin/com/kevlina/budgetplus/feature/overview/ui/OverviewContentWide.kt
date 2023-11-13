package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AppTheme

@Composable
internal fun OverviewContentWide(
    uiState: OverviewContentUiState,
    navigator: Navigator,
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {

        OverviewHeader(
            uiState = uiState.headerUiState,
            navigator = navigator,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        )

        OverviewList(
            uiState = uiState.listUiState,
            navigator = navigator,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        )
    }
}

@Preview
@Composable
private fun OverviewContentWide_Preview() = AppTheme {
    OverviewContentWide(
        uiState = OverviewContentUiState.preview,
        navigator = Navigator.empty,
    )
}