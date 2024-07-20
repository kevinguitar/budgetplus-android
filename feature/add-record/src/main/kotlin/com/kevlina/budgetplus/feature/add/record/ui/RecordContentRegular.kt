package com.kevlina.budgetplus.feature.add.record.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme

@Composable
internal fun RecordContentRegular(
    uiState: RecordContentUiState,
    modifier: Modifier = Modifier,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxHeight()
    ) {

        RecordInfo(
            uiState = uiState.recordInfoUiState,
            modifier = Modifier
                .weight(1F)
                .width(AppTheme.containerMaxWidth)
                .padding(horizontal = 16.dp)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .alpha(AppTheme.DIVIDER_ALPHA)
                .background(color = LocalAppColors.current.dark)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalAppColors.current.lightBg)
        ) {

            Calculator(
                uiState = uiState.calculatorUiState,
                modifier = Modifier
                    .width(AppTheme.containerMaxWidth)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .background(
                        color = LocalAppColors.current.light,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}

@Preview(heightDp = 700)
@Composable
private fun RecordContentRegular_Preview() = AppTheme(themeColors = ThemeColors.Barbie) {
    RecordContentRegular(
        uiState = RecordContentUiState.preview,
        modifier = Modifier.background(LocalAppColors.current.light)
    )
}