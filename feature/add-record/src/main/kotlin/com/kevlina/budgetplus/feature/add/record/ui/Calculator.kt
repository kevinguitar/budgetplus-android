package com.kevlina.budgetplus.feature.add.record.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.Surface
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.thenIf
import com.kevlina.budgetplus.feature.add.record.CalculatorViewModel
import com.kevlina.budgetplus.feature.add.record.R
import com.kevlina.budgetplus.feature.speak.record.ui.SpeakToRecordButton
import com.kevlina.budgetplus.feature.speak.record.ui.SpeakToRecordButtonState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import com.kevlina.budgetplus.core.common.R as coreCommonR

enum class CalculatorButton(val text: Char) {
    Seven('7'), Four('4'),
    Eight('8'), Five('5'),
    Nine('9'), Six('6'),
    Divide('÷'), Multiply('×'),

    One('1'), Dot('.'),
    Two('2'), Zero('0'),
    Three('3'), Delete('<'),
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
    state: CalculatorState,
    adaptiveButton: Boolean,
    modifier: Modifier = Modifier,
) {
    val needEvaluate by state.needEvaluate.collectAsStateWithLifecycle(initialValue = false)

    Column(
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        modifier = modifier
    ) {

        calcButtons.chunked(calcButtons.size / 2).forEachIndexed { index, rows ->

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
                                onClick = { state.onInput(btn) }
                            )
                        }
                    }
                }

                when (index) {
                    0 -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                            modifier = Modifier.weight(1F)
                        ) {
                            SpeakToRecordButton(
                                state = state.speakToRecordButtonState,
                                isAdaptive = adaptiveButton,
                            )

                            ClearBtn(
                                isAdaptive = adaptiveButton,
                                onClick = {
                                    state.onCalculatorAction(CalculatorAction.Clear)
                                }
                            )
                        }
                    }

                    1 -> DoneBtn(
                        needEvaluate = needEvaluate,
                        onClick = {
                            state.onCalculatorAction(
                                if (needEvaluate) {
                                    CalculatorAction.Evaluate
                                } else {
                                    CalculatorAction.Ok
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.CalculatorBtnContainer(
    isAdaptive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = LocalAppColors.current.primary,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .weight(1F)
            .thenIf(isAdaptive) { Modifier.fillMaxWidth() }
            .thenIf(!isAdaptive) { Modifier.aspectRatio(1F) },
        shape = CircleShape,
        color = color,
        content = content
    )
}

@Composable
private fun ColumnScope.CalculatorBtn(
    button: CalculatorButton,
    isAdaptive: Boolean,
    onClick: () -> Unit,
) {
    CalculatorBtnContainer(
        isAdaptive = isAdaptive,
        onClick = onClick
    ) {
        when (button) {
            CalculatorButton.Delete -> Icon(
                imageVector = Icons.AutoMirrored.Rounded.Backspace,
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

@Composable
private fun ColumnScope.ClearBtn(
    isAdaptive: Boolean,
    onClick: () -> Unit,
) {
    CalculatorBtnContainer(
        isAdaptive = isAdaptive,
        onClick = onClick,
        color = LocalAppColors.current.dark
    ) {
        Text(
            text = "AC",
            textAlign = TextAlign.Center,
            fontSize = FontSize.Header,
            fontWeight = FontWeight.Bold,
            color = LocalAppColors.current.light
        )
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

@Stable
internal data class CalculatorState(
    val needEvaluate: Flow<Boolean>,
    val speakToRecordButtonState: SpeakToRecordButtonState,
    val onInput: (CalculatorButton) -> Unit,
    val onCalculatorAction: (CalculatorAction) -> Unit,
) {
    companion object {
        val preview = CalculatorState(
            needEvaluate = MutableStateFlow(false),
            speakToRecordButtonState = SpeakToRecordButtonState.preview,
            onInput = {},
            onCalculatorAction = {}
        )
    }
}

internal fun CalculatorViewModel.toState() = CalculatorState(
    needEvaluate = needEvaluate,
    speakToRecordButtonState = SpeakToRecordButtonState(
        onTap = speakToRecordViewModel::onButtonTap,
        onReleased = speakToRecordViewModel::onButtonReleased,
        showLoader = speakToRecordViewModel.showLoader,
        showRecordingDialog = speakToRecordViewModel.showRecordingDialog,
        highlightRecordButton = speakToRecordViewModel::highlightRecordButton,
        showRecordPermissionHint = speakToRecordViewModel::showRecordPermissionHint,
    ),
    onInput = ::onInput,
    onCalculatorAction = ::onCalculatorAction
)

@Preview
@Composable
private fun Calculator_Preview() = AppTheme {
    Calculator(
        state = CalculatorState.preview,
        adaptiveButton = false,
        modifier = Modifier
            .background(LocalAppColors.current.light)
            .padding(all = 16.dp)
    )
}

@Preview(widthDp = 400, heightDp = 600)
@Composable
private fun CalculatorAdaptive_Preview() = AppTheme(themeColors = ThemeColors.Lavender) {
    Calculator(
        state = CalculatorState.preview,
        adaptiveButton = true,
        modifier = Modifier
            .background(LocalAppColors.current.light)
            .padding(all = 16.dp)
    )
}