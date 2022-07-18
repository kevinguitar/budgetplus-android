package com.kevingt.moneybook.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
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
        singleLine = true,
        cursorBrush = SolidColor(LocalAppColors.current.dark),
        decorationBox = @Composable { innerTextField ->
            if (value.isEmpty() && placeholder != null) {
                Text(
                    text = placeholder,
                    textAlign = TextAlign.End,
                    fontSize = fontSize,
                    color = LocalAppColors.current.dark,
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
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
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
        singleLine = true,
        cursorBrush = SolidColor(LocalAppColors.current.dark),
        decorationBox = @Composable { innerTextField ->
            if (value.text.isEmpty() && placeholder != null) {
                Text(
                    text = placeholder,
                    textAlign = TextAlign.End,
                    fontSize = fontSize,
                    color = LocalAppColors.current.dark,
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
            .padding(all = 12.dp)
    ) {

        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            color = LocalAppColors.current.dark
        )

        content()
    }
}