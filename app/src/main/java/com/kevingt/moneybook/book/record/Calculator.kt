package com.kevingt.moneybook.book.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import net.objecthunter.exp4j.ExpressionBuilder

@Composable
fun Calculator() {

    val viewModel = hiltViewModel<RecordViewModel>()

    val priceText by viewModel.priceText.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            TextField(value = priceText, onValueChange = viewModel::setPriceText)

            Button(onClick = { viewModel.setPriceText("") }) {
                Text(
                    text = "AC",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CalculatorButton.values()) { btn ->
                CalculatorBtn(button = btn) {
                    if (btn == CalculatorButton.Equal) {
                        val expression = ExpressionBuilder(priceText).build()
                        viewModel.setPriceText(expression.evaluate().toString())
                    } else {
                        viewModel.setPriceText(priceText + btn.text)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorBtn(
    button: CalculatorButton,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
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

@Preview
@Composable
private fun Calculator_Preview() = Calculator()

private enum class CalculatorButton(val text: String) {
    Seven("7"), Eight("8"), Nine("9"), Divide("/"),
    Four("4"), Five("5"), Six("6"), Multiply("*"),
    One("1"), Two("2"), Three("3"), Minus("-"),
    Zero("0"), Dot("."), Equal("="), Plus("+")
}