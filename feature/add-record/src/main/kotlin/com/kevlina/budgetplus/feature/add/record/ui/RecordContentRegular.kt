package com.kevlina.budgetplus.feature.add.record.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun RecordContentRegular(
    uiState: RecordContentUiState,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints {
        val maxCalculatorHeight = maxHeight / 2
        var calculatorHeightPx = remember { mutableIntStateOf(0) }

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

            val isAdaptiveButton = with(LocalDensity.current) {
                // Give a small room for rounding
                @Suppress("MagicNumber")
                calculatorHeightPx.intValue + 10 > maxCalculatorHeight.roundToPx()
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxCalculatorHeight)
                    .background(LocalAppColors.current.lightBg)
                    .onSizeChanged { calculatorHeightPx.intValue = it.height }
            ) {
                Calculator(
                    state = uiState.calculatorState,
                    adaptiveButton = isAdaptiveButton,
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
}

@PreviewScreenSizes
@Composable
private fun RecordContentRegular_Preview() = AppTheme(themeColors = ThemeColors.Barbie) {
    RecordContentRegular(
        uiState = RecordContentUiState.preview,
        modifier = Modifier.background(LocalAppColors.current.light)
    )
}