package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun ColorToneShowcase(
    colors: List<Color>,
    outlineColor: Color,
    modifier: Modifier = Modifier,
) {

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .border(
                width = 1.dp,
                color = outlineColor,
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
}

@Preview
@Composable
private fun ColorToneCard_Preview() = ColorToneShowcase(
    colors = listOf(Color.Blue, Color.Cyan, Color.DarkGray),
    outlineColor = Color.Black,
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
)