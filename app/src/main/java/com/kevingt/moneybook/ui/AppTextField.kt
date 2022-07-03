package com.kevingt.moneybook.ui

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    singleLine: Boolean = true,
    backgroundColor: Color = LocalAppColors.current.primaryLight,
    textColor: Color = LocalAppColors.current.dark,
) = TextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    enabled = enabled,
    textStyle = textStyle,
    placeholder = placeholder,
    leadingIcon = leadingIcon,
    keyboardOptions = keyboardOptions,
    singleLine = singleLine,
    shape = RoundedCornerShape(12.dp),
    colors = AppTextFieldColors(backgroundColor, textColor)
)

@Composable
fun AppTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    singleLine: Boolean = true,
    backgroundColor: Color = LocalAppColors.current.primaryLight,
    textColor: Color = LocalAppColors.current.dark,
) = TextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    enabled = enabled,
    textStyle = textStyle,
    placeholder = placeholder,
    leadingIcon = leadingIcon,
    keyboardOptions = keyboardOptions,
    singleLine = singleLine,
    shape = RoundedCornerShape(12.dp),
    colors = AppTextFieldColors(backgroundColor, textColor)
)

@Immutable
private class AppTextFieldColors(
    private val backgroundColor: Color,
    private val textColor: Color
) : TextFieldColors {

    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(backgroundColor)

    @Composable
    override fun cursorColor(isError: Boolean): State<Color> =
        rememberUpdatedState(textColor)

    @Composable
    override fun indicatorColor(
        enabled: Boolean,
        isError: Boolean,
        interactionSource: InteractionSource
    ): State<Color> = rememberUpdatedState(Color.Unspecified)

    @Composable
    override fun labelColor(
        enabled: Boolean,
        error: Boolean,
        interactionSource: InteractionSource
    ): State<Color> = rememberUpdatedState(Color.Unspecified)

    @Composable
    override fun leadingIconColor(enabled: Boolean, isError: Boolean): State<Color> =
        rememberUpdatedState(Color.Unspecified)

    @Composable
    override fun placeholderColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(textColor.copy(alpha = 0.4F))

    @Composable
    override fun textColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(textColor)

    @Composable
    override fun trailingIconColor(enabled: Boolean, isError: Boolean): State<Color> =
        rememberUpdatedState(Color.Unspecified)
}