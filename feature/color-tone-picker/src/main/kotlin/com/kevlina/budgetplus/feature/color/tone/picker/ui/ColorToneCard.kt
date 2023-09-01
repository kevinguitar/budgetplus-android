package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.ColorTone
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.darken

private const val CARD_TEXT_DARKEN_FACTOR = 0.7F

@Composable
internal fun ColorToneCard(
    colorTone: ColorTone,
    modifier: Modifier = Modifier,
) {

    val themeColors = colorTone.themeColors

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(AppTheme.cardShape)
            .background(themeColors.lightBg)
            .padding(vertical = 16.dp, horizontal = 32.dp)
    ) {

        Text(
            text = colorTone.name,
            fontSize = FontSize.Large,
            fontWeight = FontWeight.SemiBold,
            color = themeColors.primary.darken(CARD_TEXT_DARKEN_FACTOR)
        )

        ColorTonePlatte(
            colorTone = colorTone,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ColorTonePlatte(
    colorTone: ColorTone,
    modifier: Modifier = Modifier,
) {

    val themeColors = colorTone.themeColors
    val colors = remember(themeColors) {
        listOf(
            themeColors.light,
            themeColors.primary,
            themeColors.dark,
        )
    }

    Canvas(
        modifier = modifier
    ) {
        val itemWidth = size.width / colors.size
        val cornerRadius = CornerRadius(16.dp.toPx())

        colors.forEachIndexed { index, color ->
            when (index) {
                0 -> drawRoundRect(
                    color = color,
                    topLeft = Offset.Zero,
                    size = Size(
                        width = itemWidth * 2,
                        height = size.height
                    ),
                    cornerRadius = cornerRadius,
                )

                colors.lastIndex -> {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x = itemWidth * (colors.size - 1), y = 0f),
                        size = Size(width = itemWidth, height = size.height),
                        cornerRadius = cornerRadius,
                    )

                    // To cover the left part of the corners
                    drawRect(
                        color = color,
                        topLeft = Offset(x = itemWidth * (colors.size - 1), y = 0f),
                        size = Size(width = itemWidth / 2, height = size.height),
                    )
                }

                else -> drawRect(
                    color = color,
                    topLeft = Offset(x = itemWidth * index, y = 0f),
                    size = Size(width = itemWidth, height = size.height),
                )
            }
        }
    }
}

@Preview
@Composable
private fun ColorToneCard_Preview() = ColorToneCard(
    colorTone = ColorTone.MilkTea,
    modifier = Modifier.fillMaxWidth()
)