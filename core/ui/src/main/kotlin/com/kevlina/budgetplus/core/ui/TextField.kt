package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors

internal const val PLACEHOLDER_ALPHA = 0.5F

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String,
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
    onDone: (KeyboardActionScope.() -> Unit)? = null,
) = TextFieldInternal(
    modifier = modifier,
    title = title,
    onTitleClick = onTitleClick,
    fontSize = fontSize
) {

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
        keyboardActions = KeyboardActions(onDone = onDone),
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

@Composable
fun TextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    onTitleClick: (() -> Unit)? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    fontSize: TextUnit = TextUnit.Unspecified,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        imeAction = ImeAction.Done
    ),
    onDone: (KeyboardActionScope.() -> Unit)? = null,
) = TextFieldInternal(
    modifier = modifier,
    title = title,
    onTitleClick = onTitleClick,
    fontSize = fontSize
) {

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        modifier = Modifier.weight(1F),
        textStyle = TextStyle(
            color = LocalAppColors.current.dark,
            textAlign = TextAlign.End,
            fontSize = fontSize
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(onDone = onDone),
        singleLine = singleLine,
        cursorBrush = SolidColor(LocalAppColors.current.dark),
        decorationBox = @Composable { innerTextField ->
            if (value.text.isEmpty() && placeholder != null) {
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

@Composable
private fun TextFieldInternal(
    modifier: Modifier,
    title: String,
    onTitleClick: (() -> Unit)? = null,
    fontSize: TextUnit,
    content: @Composable RowScope.() -> Unit,
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

        content()
    }
}

@Preview
@Composable
private fun TextField_Preview() = AppTheme(themeColors = ThemeColors.Barbie) {
    var input by remember { mutableStateOf("") }
    TextField(
        value = input,
        onValueChange = { input = it },
        title = "Username",
        placeholder = "Your username",
    )
}