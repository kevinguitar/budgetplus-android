package com.kevingt.moneybook.book.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.ui.AppText
import com.kevingt.moneybook.ui.AppTheme
import com.kevingt.moneybook.ui.FontSize
import com.kevingt.moneybook.ui.LocalAppColors
import com.kevingt.moneybook.utils.dollar
import com.kevingt.moneybook.utils.rippleClick
import com.kevingt.moneybook.utils.roundUpPercentageText

@Composable
fun OverviewGroup(
    category: String,
    records: List<Record>,
    totalPrice: Double,
    color: Color,
    isLast: Boolean,
    onClick: () -> Unit
) {

    val sum = records.sumOf { it.price }
    val percentage = sum * 100 / totalPrice

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

            Box(
                modifier = Modifier
                    .weight(1F)
                    .padding(vertical = 16.dp)
            ) {

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

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth((percentage / 100).toFloat())
                        .height(24.dp)
                        .align(Alignment.CenterStart)
                        .background(
                            color = color.copy(alpha = 0.1F),
                            shape = RoundedCornerShape(
                                topEndPercent = 50,
                                bottomEndPercent = 50
                            )
                        )
                )
            }

            AppText(
                text = "${percentage.roundUpPercentageText}%",
                color = LocalAppColors.current.light,
                fontSize = FontSize.Small,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        color = color,
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
        color = Color.Red,
        isLast = false,
        onClick = {}
    )
}