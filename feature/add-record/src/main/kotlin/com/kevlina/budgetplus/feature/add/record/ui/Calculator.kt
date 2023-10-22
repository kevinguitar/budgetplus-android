package com.kevlina.budgetplus.feature.add.record.ui

import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.Surface
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.feature.add.record.CalculatorViewModel
import com.kevlina.budgetplus.feature.add.record.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.kevlina.budgetplus.core.common.R as coreCommonR

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
    Clear, Evaluate, Ok;
}

private val horizontalSpacing = 12.dp
private val verticalSpacing = 8.dp
private val calcButtons = CalculatorButton.entries.toList()

@Composable
internal fun Calculator(
    uiState: CalculatorUiState,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    val needEvaluate by uiState.needEvaluate.collectAsStateWithLifecycle()

    Column(
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        modifier = modifier
    ) {

        calcButtons.chunked(calcButtons.size / 2).forEachIndexed { index, rows ->

            Row(
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
                modifier = if (uiState.adaptiveButton) {
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
                                isAdaptive = uiState.adaptiveButton,
                                onClick = { uiState.onInput(btn) }
                            )
                        }
                    }
                }

                when (index) {
                    0 -> ClearBtn(onClick = {
                        uiState.onCalculatorAction(context, CalculatorAction.Clear)
                    })

                    1 -> DoneBtn(
                        needEvaluate = needEvaluate,
                        onClick = {
                            uiState.onCalculatorAction(context, if (needEvaluate) {
                                CalculatorAction.Evaluate
                            } else {
                                CalculatorAction.Ok
                            })
                        }
                    )
                }
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
                    contentDescription = stringResource(id = coreCommonR.string.cta_delete),
                    tint = LocalAppColors.current.light
                )

                CalculatorButton.Plus -> Image(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalAppColors.current.light)
                )

                CalculatorButton.Minus -> Image(
                    painter = painterResource(id = R.drawable.ic_minus),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalAppColors.current.light)
                )

                CalculatorButton.Multiply -> Image(
                    painter = painterResource(id = R.drawable.ic_multiply),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalAppColors.current.light)
                )

                CalculatorButton.Divide -> Image(
                    painter = painterResource(id = R.drawable.ic_divide),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalAppColors.current.light)
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
private fun RowScope.ClearBtn(onClick: () -> Unit) {

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
                text = "AC",
                textAlign = TextAlign.Center,
                fontSize = FontSize.Header,
                fontWeight = FontWeight.Bold,
                color = LocalAppColors.current.light
            )
        }
    }
}

@Composable
private fun RowScope.DoneBtn(
    needEvaluate: Boolean,
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

            if (needEvaluate) {
                Image(
                    painter = painterResource(id = R.drawable.ic_equal),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalAppColors.current.light)
                )
            } else {
                Text(
                    text = "OK",
                    textAlign = TextAlign.Center,
                    fontSize = FontSize.Header,
                    fontWeight = FontWeight.Bold,
                    color = LocalAppColors.current.light
                )
            }
        }
    }
}

@Stable
internal data class CalculatorUiState(
    val needEvaluate: StateFlow<Boolean>,
    val adaptiveButton: Boolean,
    val onInput: (CalculatorButton) -> Unit,
    val onCalculatorAction: (Context, CalculatorAction) -> Unit,
) {
    companion object {
        val preview = CalculatorUiState(
            needEvaluate = MutableStateFlow(false),
            adaptiveButton = false,
            onInput = {},
            onCalculatorAction = { _, _ -> }
        )
    }
}

internal fun CalculatorViewModel.toUiState(adaptiveButton: Boolean = false) = CalculatorUiState(
    needEvaluate = needEvaluate,
    adaptiveButton = adaptiveButton,
    onInput = ::onInput,
    onCalculatorAction = ::onCalculatorAction
)

@Preview(showBackground = true)
@Composable
private fun Calculator_Preview() = AppTheme {
    Calculator(uiState = CalculatorUiState.preview)
}