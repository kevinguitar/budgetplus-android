package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.record.vm.CalculatorViewModel
import com.kevingt.moneybook.ui.*

enum class CalculatorButton(val text: String) {
    Seven("7"), Four("4"),
    Eight("8"), Five("5"),
    Nine("9"), Six("6"),
    Divide("÷"), Multiply("×"),

    One("1"), DoubleZero("00"),
    Two("2"), Zero("0"),
    Three("3"), Dot("."),
    Minus("-"), Plus("+");
}

enum class CalculatorAction(val text: String) {
    Clear("AC"), Equal("="), Delete("");
}

@Composable
fun Calculator(viewModel: CalculatorViewModel) {

    val priceText by viewModel.priceText.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AppTextField(
                value = priceText,
                onValueChange = {},
                fontSize = 20.sp,
                enabled = false,
                title = "$",
                modifier = Modifier.weight(1F)
            )

            IconButton(
                onClick = { viewModel.onAction(CalculatorAction.Delete) }
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_backspace),
                    contentDescription = stringResource(id = R.string.cta_delete),
                    tint = LocalAppColors.current.dark,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            CalculatorButton.values().toList().chunked(8).forEachIndexed { index, rows ->

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(intrinsicSize = IntrinsicSize.Min)
                ) {

                    rows.chunked(2).forEach { btns ->

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                            btns.forEach { btn ->

                                CalculatorBtn(
                                    text = btn.text,
                                    onClick = { viewModel.onInput(btn) }
                                )
                            }
                        }
                    }

                    val action = CalculatorAction.values()[index]
                    CalculatorActionBtn(
                        text = action.text,
                        onClick = { viewModel.onAction(action) },
                    )
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

            AppText(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = FontSize.Header,
                fontWeight = FontWeight.Bold,
                color = LocalAppColors.current.light
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CalculatorActionBtn(
    text: String,
    onClick: () -> Unit
) {

    Surface(
        onClick = onClick,
        modifier = Modifier
            .width(48.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(50),
        color = LocalAppColors.current.dark
    ) {

        Box(contentAlignment = Alignment.Center) {

            AppText(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = FontSize.Header,
                fontWeight = FontWeight.Bold,
                color = LocalAppColors.current.light
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Calculator_Preview() = AppTheme {
    Calculator(CalculatorViewModel(null))
}