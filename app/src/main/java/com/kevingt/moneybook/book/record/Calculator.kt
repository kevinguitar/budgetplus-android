package com.kevingt.moneybook.book.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevingt.moneybook.book.record.vm.CalculatorViewModel
import com.kevingt.moneybook.ui.LocalAppColors

enum class CalculatorButton(val text: String) {
    Seven("7"), Eight("8"), Nine("9"), Divide("÷"),
    Four("4"), Five("5"), Six("6"), Multiply("×"),
    One("1"), Two("2"), Three("3"), Minus("-"),
    Zero("0"), Dot("."), Equal("="), Plus("+")
}

@Composable
fun Calculator(viewModel: CalculatorViewModel) {

    val priceText by viewModel.priceText.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            Text(
                text = priceText,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.h6,
                color = LocalAppColors.current.dark,
                modifier = Modifier
                    .weight(1F)
                    .align(Alignment.CenterVertically)
                    .background(
                        color = LocalAppColors.current.primaryLight,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )

            CalculatorBtn(
                text = "AC",
                onClick = { viewModel.clearPrice() }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton.values().toList()
                .chunked(4)
                .forEach { btnCol ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        btnCol.forEach { btn ->
                            CalculatorBtn(
                                text = btn.text,
                                onClick = { viewModel.onInput(btn) }
                            )
                        }
                    }
                }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CalculatorBtn(
    text: String,
    onClick: () -> Unit
) {

    Surface(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        color = LocalAppColors.current.primary
    ) {

        Box(contentAlignment = Alignment.Center) {

            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = LocalAppColors.current.light
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Calculator_Preview() = Calculator(CalculatorViewModel())