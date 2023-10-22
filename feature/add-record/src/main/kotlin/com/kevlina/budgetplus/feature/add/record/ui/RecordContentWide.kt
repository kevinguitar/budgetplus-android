package com.kevlina.budgetplus.feature.add.record.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.ads.AdsBanner
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
internal fun RecordContentWide(
    uiState: RecordContentUiState,
    modifier: Modifier = Modifier,
) {

    val showAds by uiState.showAds.collectAsStateWithLifecycle()

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {

        Column(modifier = Modifier.weight(1F)) {

            RecordInfo(
                uiState = uiState.recordInfoUiState,
                modifier = Modifier.weight(1F)
            )

            if (showAds) {
                AdsBanner()
            }
        }

        Calculator(
            uiState = uiState.calculatorUiState,
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
                .padding(vertical = 16.dp)
        )
    }
}

@Preview(widthDp = 800, heightDp = 360)
@Preview(widthDp = 960, heightDp = 400)
@Composable
private fun RecordContentWide_Preview() = AppTheme(themeColors = ThemeColors.NemoSea) {
    RecordContentWide(
        uiState = RecordContentUiState(
            recordInfoUiState = RecordInfoUiState.preview,
            calculatorUiState = CalculatorUiState.preview.copy(adaptiveButton = true),
            showAds = MutableStateFlow(false)
        ),
        modifier = Modifier.background(LocalAppColors.current.light)
    )
}