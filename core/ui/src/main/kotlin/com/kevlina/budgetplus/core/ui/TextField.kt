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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.LocalAppColors

private const val PLACEHOLDER_ALPHA = 0.5F

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    placeholder: String? = null,
    readOnly: Boolean = false,
    fontSize: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        imeAction = ImeAction.Done
    ),
    onDone: (KeyboardActionScope.() -> Unit)? = null
) = TextFieldInternal(modifier, title, fontSize) {

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
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
        singleLine = true,
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
    placeholder: String? = null,
    enabled: Boolean = true,
    fontSize: TextUnit = TextUnit.Unspecified,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        imeAction = ImeAction.Done
    ),
    onDone: (KeyboardActionScope.() -> Unit)? = null
) = TextFieldInternal(modifier, title, fontSize) {

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = Modifier.weight(1F),
        textStyle = TextStyle(
            color = LocalAppColors.current.dark,
            textAlign = TextAlign.End,
            fontSize = fontSize
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(onDone = onDone),
        singleLine = true,
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
    fontSize: TextUnit,
    content: @Composable RowScope.() -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(56.dp)
            .background(
                color = LocalAppColors.current.lightBg,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSize,
        )

        content()
    }
}