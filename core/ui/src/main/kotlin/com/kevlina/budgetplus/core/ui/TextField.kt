package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors

internal const val PLACEHOLDER_ALPHA = 0.5F

@Composable
fun TextField(
    state: TextFieldState,
    title: String,
    modifier: Modifier = Modifier,
    onTitleClick: (() -> Unit)? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    fontSize: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        imeAction = ImeAction.Done
    ),
    onDone: (() -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(56.dp)
            .background(
                color = LocalAppColors.current.lightBg,
                shape = RoundedCornerShape(AppTheme.cornerRadius)
            )
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSize,
            modifier = Modifier.thenIfNotNull(onTitleClick) {
                Modifier.rippleClick(
                    borderless = true,
                    onClick = it
                )
            }
        )

        BasicTextField(
            state = state,
            enabled = enabled,
            readOnly = readOnly,
            modifier = Modifier.weight(1F),
            textStyle = TextStyle(
                color = LocalAppColors.current.dark,
                textAlign = TextAlign.End,
                fontSize = fontSize,
                letterSpacing = letterSpacing
            ),
            keyboardOptions = keyboardOptions,
            onKeyboardAction = if (onDone == null) {
                null
            } else {
                { onDone.invoke() }
            },
            lineLimits = if (singleLine) {
                TextFieldLineLimits.SingleLine
            } else {
                TextFieldLineLimits.MultiLine()
            },
            cursorBrush = SolidColor(LocalAppColors.current.dark),
            decorator = @Composable { innerTextField ->
                if (state.text.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        textAlign = TextAlign.End,
                        fontSize = fontSize,
                        modifier = Modifier.alpha(PLACEHOLDER_ALPHA)
                    )
                }

                innerTextField()
            }
        )
    }
}

// Workaround to fix the text layout issue in TextFieldState overload.
// See: https://issuetracker.google.com/issues/384009738
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    onTitleClick: (() -> Unit)? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    fontSize: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        imeAction = ImeAction.Done
    ),
    onDone: (() -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(56.dp)
            .background(
                color = LocalAppColors.current.lightBg,
                shape = RoundedCornerShape(AppTheme.cornerRadius)
            )
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSize,
            modifier = Modifier.thenIfNotNull(onTitleClick) {
                Modifier.rippleClick(
                    borderless = true,
                    onClick = it
                )
            }
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            modifier = Modifier.weight(1F),
            textStyle = TextStyle(
                color = LocalAppColors.current.dark,
                textAlign = TextAlign.End,
                fontSize = fontSize,
                letterSpacing = letterSpacing
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions { onDone?.invoke() },
            singleLine = singleLine,
            cursorBrush = SolidColor(LocalAppColors.current.dark),
            decorationBox = @Composable { innerTextField ->
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        textAlign = TextAlign.End,
                        fontSize = fontSize,
                        modifier = Modifier.alpha(PLACEHOLDER_ALPHA)
                    )
                }

                innerTextField()
            }
        )
    }
}

@Preview
@Composable
private fun TextField_Preview() = AppTheme(themeColors = ThemeColors.Barbie) {
    val input = rememberTextFieldState("")
    TextField(
        state = input,
        title = "Username",
        placeholder = "Your username",
    )
}