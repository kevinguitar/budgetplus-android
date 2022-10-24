package com.kevlina.budgetplus.ui

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

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
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
) = AppTextFieldInternal(modifier, title, fontSize) {

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
            if (value.isEmpty() && placeholder != null) {
                AppText(
                    text = placeholder,
                    textAlign = TextAlign.End,
                    fontSize = fontSize,
                    modifier = Modifier.alpha(0.5F)
                )
            }

            innerTextField()
        }
    )
}

@Composable
fun AppTextField(
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
) = AppTextFieldInternal(modifier, title, fontSize) {

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
                AppText(
                    text = placeholder,
                    textAlign = TextAlign.End,
                    fontSize = fontSize,
                    modifier = Modifier.alpha(0.5F)
                )
            }

            innerTextField()
        }
    )
}

@Composable
private fun AppTextFieldInternal(
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
                color = LocalAppColors.current.primaryLight,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp)
    ) {

        AppText(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSize,
        )

        content()
    }
}