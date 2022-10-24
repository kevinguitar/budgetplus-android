package com.kevlina.budgetplus.book.overview

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.AppTheme
import com.kevlina.budgetplus.ui.FontSize
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.utils.darken
import com.kevlina.budgetplus.utils.dollar
import com.kevlina.budgetplus.utils.rippleClick
import com.kevlina.budgetplus.utils.roundUpPercentageText

//TODO: records param is unstable
@Composable
fun OverviewGroup(
    category: String,
    records: List<Record>,
    totalPrice: Double,
    color: Color,
    isLast: Boolean,
    onClick: () -> Unit,
) {

    val sum = records.sumOf { it.price }
    val percentage = sum / totalPrice

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .rippleClick(onClick = onClick)
            .padding(horizontal = 16.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            BoxWithConstraints(
                modifier = Modifier
                    .weight(1F)
                    .padding(vertical = 16.dp)
            ) {

                val progress = calcProgressWidth(maxWidth, percentage)
                val width by animateDpAsState(
                    targetValue = progress,
                    animationSpec = tween(durationMillis = 500)
                )

                Spacer(
                    modifier = Modifier
                        .width(width)
                        .height(24.dp)
                        .align(Alignment.CenterStart)
                        .background(
                            color = color.copy(alpha = 0.7F),
                            shape = RoundedCornerShape(
                                topEndPercent = 50,
                                bottomEndPercent = 50
                            )
                        )
                )

                AppText(
                    text = category,
                    fontSize = FontSize.SemiLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                AppText(
                    text = sum.dollar,
                    fontSize = FontSize.SemiLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            AppText(
                text = "${(percentage * 100).roundUpPercentageText}%",
                color = LocalAppColors.current.light,
                fontSize = FontSize.Small,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        color = color.darken(1.2F),
                        shape = CircleShape
                    )
                    .width(48.dp)
                    .padding(vertical = 4.dp)
            )
        }

        if (!isLast) {
            Divider(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(color = LocalAppColors.current.primary)
            )
        }
    }
}

private const val PROGRESS_AMP = 0.2F

private fun calcProgressWidth(maxWidth: Dp, ratio: Double): Dp {
    val base = maxWidth * ratio.toFloat()
    val amp = (maxWidth - base) * PROGRESS_AMP
    return base + amp
}

@Preview(showBackground = true)
@Composable
private fun OverviewGroup_Preview() = AppTheme {
    OverviewGroup(
        category = "日常",
        records = listOf(
            Record(price = 10.3),
            Record(price = 42.43),
            Record(price = 453.1),
        ),
        totalPrice = 1043.5,
        color = overviewColors[0],
        isLast = false,
        onClick = {}
    )
}