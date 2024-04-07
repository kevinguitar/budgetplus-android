package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.LocalAppColors

@Composable
fun SearchField(
    keyword: String,
    onKeywordChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        imeAction = ImeAction.Done
    ),
    onDone: (KeyboardActionScope.() -> Unit)? = null,
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

        Icon(
            imageVector = Icons.Rounded.Search,
            tint = LocalAppColors.current.dark,
            modifier = Modifier.size(24.dp)
        )

        BasicTextField(
            value = keyword,
            onValueChange = onKeywordChanged,
            modifier = Modifier.weight(1F),
            textStyle = TextStyle(
                color = LocalAppColors.current.dark,
                textAlign = TextAlign.End,
                fontSize = FontSize.Large,
                letterSpacing = TextUnit.Unspecified
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(onDone = onDone),
            singleLine = true,
            cursorBrush = SolidColor(LocalAppColors.current.dark),
            decorationBox = @Composable { innerTextField ->
                if (keyword.isEmpty() && hint != null) {
                    Text(
                        text = hint,
                        textAlign = TextAlign.End,
                        fontSize = FontSize.Large,
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
private fun SearchField_Preview() = AppTheme {
    SearchField(
        keyword = "",
        onKeywordChanged = {},
        hint = "USD"
    )
}