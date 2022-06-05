package com.kevingt.moneybook.book.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevingt.moneybook.book.record.vm.CalculatorViewModel

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

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            Text(
                text = priceText,
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .weight(1F)
                    .align(Alignment.CenterVertically)
                    .background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(all = 8.dp)
            )

            Button(onClick = { viewModel.clearPrice() }) {
                Text(
                    text = "AC",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton.values().toList()
                .chunked(4)
                .forEach { btnCol ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        btnCol.forEach { btn ->
                            CalculatorBtn(
                                button = btn,
                                modifier = Modifier.weight(1F),
                                onClick = { viewModel.onInput(btn) }
                            )
                        }
                    }
                }
        }
    }
}

@Composable
private fun CalculatorBtn(
    button: CalculatorButton,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
    ) {

        Text(
            text = button.text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Calculator_Preview() = Calculator(CalculatorViewModel())