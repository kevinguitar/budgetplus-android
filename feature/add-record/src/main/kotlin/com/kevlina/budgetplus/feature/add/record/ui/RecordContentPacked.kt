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
    state: RecordContentState,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .containerPadding()
    ) {

        RecordInfo(
            state = state.recordInfoState,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Calculator(
            state = state.calculatorState,
            adaptiveButton = false,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
    }
}

@Preview(widthDp = 360, heightDp = 480)
@Preview(widthDp = 360, heightDp = 640)
@Preview(widthDp = 480, heightDp = 640)
@Composable
private fun RecordContentPacked_Preview() = AppTheme(themeColors = ThemeColors.Countryside) {
    RecordContentPacked(
        state = RecordContentState(
            recordInfoState = RecordInfoState.preview.copy(scrollable = false),
            calculatorState = CalculatorState.preview,
        ),
        modifier = Modifier.background(LocalAppColors.current.light)
    )
}