package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.ColorTone
import com.kevlina.budgetplus.core.ui.TOP_BAR_DARKEN_FACTOR
import com.kevlina.budgetplus.core.ui.clickableWithoutRipple
import com.kevlina.budgetplus.core.ui.darken

@Composable
internal fun ColorToneCard(
    colorTone: ColorTone,
    isPremium: Boolean,
    unlockPremium: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val themeColors = colorTone.themeColors
    val colors = remember(themeColors) {
        listOf(
            themeColors.light,
            themeColors.lightBg,
            themeColors.primary,
            themeColors.dark,
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = themeColors.primary.darken(TOP_BAR_DARKEN_FACTOR),
                    shape = RoundedCornerShape(ColorToneConstants.CardCornerRadius)
                )
        ) {
            val itemWidth = size.width / colors.size
            val cornerRadiusPx = CornerRadius(ColorToneConstants.CardCornerRadius.toPx())

            colors.forEachIndexed { index, color ->
                when (index) {
                    0 -> drawRoundRect(
                        color = color,
                        topLeft = Offset.Zero,
                        size = Size(
                            width = itemWidth * 2,
                            height = size.height
                        ),
                        cornerRadius = cornerRadiusPx,
                    )

                    colors.lastIndex -> {
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(x = itemWidth * (colors.size - 1), y = 0f),
                            size = Size(width = itemWidth, height = size.height),
                            cornerRadius = cornerRadiusPx,
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

        if (colorTone.requiresPremium && !isPremium) {
            UnlockAnimator(
                color = themeColors.dark,
                modifier = Modifier
                    .size(90.dp)
                    .clickableWithoutRipple(onClick = unlockPremium)
            )
        }
    }
}

@Preview
@Composable
private fun ColorToneCard_Preview() = ColorToneCard(
    colorTone = ColorTone.MilkTea,
    isPremium = false,
    unlockPremium = {},
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
)