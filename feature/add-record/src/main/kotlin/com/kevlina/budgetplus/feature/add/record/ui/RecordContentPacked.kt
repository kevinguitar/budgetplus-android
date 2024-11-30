package com.kevlina.budgetplus.feature.add.record.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.containerPadding

@Composable
internal fun RecordContentPacked(
    uiState: RecordContentUiState,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .containerPadding()
    ) {

        RecordInfo(
            uiState = uiState.recordInfoUiState,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Calculator(
            state = uiState.calculatorState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
    }
}

@Preview(heightDp = 700)
@Composable
private fun RecordContentPacked_Preview() = AppTheme(themeColors = ThemeColors.Countryside) {
    RecordContentPacked(
        uiState = RecordContentUiState.preview,
        modifier = Modifier.background(LocalAppColors.current.light)
    )
}