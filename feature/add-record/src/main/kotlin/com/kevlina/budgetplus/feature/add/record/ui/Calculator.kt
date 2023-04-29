package com.kevlina.budgetplus.feature.add.record.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.Surface
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.feature.add.record.CalculatorViewModel

enum class CalculatorButton(val text: Char) {
    Seven('7'), Four('4'),
    Eight('8'), Five('5'),
    Nine('9'), Six('6'),
    Divide('÷'), Multiply('×'),

    One('1'), Zero('0'),
    Two('2'), Dot('.'),
    Three('3'), Back('<'),
    Minus('-'), Plus('+');
}

enum class CalculatorAction {
    Clear, Ok;
}

private val horizontalSpacing = 12.dp
private val verticalSpacing = 8.dp

@Composable
fun Calculator(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier,
    adaptiveButton: Boolean = false,
) {

    val context = LocalContext.current
    val needEvaluate by viewModel.needEvaluate.collectAsStateWithLifecycle()

    Column(
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        modifier = modifier
    ) {

        CalculatorButton.values().toList().chunked(8).forEachIndexed { index, rows ->

            Row(
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
                modifier = if (adaptiveButton) {
                    Modifier
                        .weight(1F)
                        .fillMaxWidth()
                } else {
                    Modifier
                        .fillMaxWidth()
                        .height(intrinsicSize = IntrinsicSize.Min)
                }
            ) {

                rows.chunked(2).forEach { btns ->

                    Column(
                        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                        modifier = Modifier.weight(1F)
                    ) {

                        btns.forEach { btn ->

                            CalculatorBtn(
                                button = btn,
                                isAdaptive = adaptiveButton,
                                onClick = { viewModel.onInput(btn) }
                            )
                        }
                    }
                }

                val action = CalculatorAction.values()[index]

                CalculatorActionBtn(
                    text = when {
                        action == CalculatorAction.Clear -> "AC"
                        needEvaluate -> "="
                        else -> "OK"
                    },
                    onClick = { viewModel.onCalculatorAction(context, action) },
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.CalculatorBtn(
    button: CalculatorButton,
    isAdaptive: Boolean,
    onClick: () -> Unit,
) {

    Surface(
        onClick = onClick,
        modifier = if (isAdaptive) {
            Modifier
                .weight(1F)
                .fillMaxWidth()
        } else {
            Modifier
                .weight(1F)
                .aspectRatio(1F)
        },
        shape = CircleShape,
        color = LocalAppColors.current.primary
    ) {

        Box(contentAlignment = Alignment.Center) {

            when (button) {
                CalculatorButton.Back -> Icon(
                    imageVector = Icons.Rounded.Backspace,
                    contentDescription = stringResource(id = R.string.cta_delete),
                    tint = LocalAppColors.current.light
                )

                else -> Text(
                    text = button.text.toString(),
                    textAlign = TextAlign.Center,
                    fontSize = FontSize.HeaderLarge,
                    fontWeight = FontWeight.Bold,
                    color = LocalAppColors.current.light
                )
            }
        }
    }
}

@Composable
private fun RowScope.CalculatorActionBtn(
    text: String,
    onClick: () -> Unit,
) {

    Surface(
        onClick = onClick,
        modifier = Modifier
            .weight(1F)
            .fillMaxHeight(),
        shape = CircleShape,
        color = LocalAppColors.current.dark
    ) {

        Box(contentAlignment = Alignment.Center) {

            Text(
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
    Calculator(viewModel = CalculatorViewModel(null, null))
}