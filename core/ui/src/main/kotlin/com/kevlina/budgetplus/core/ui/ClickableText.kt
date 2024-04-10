package com.kevlina.budgetplus.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.kevlina.budgetplus.core.theme.LocalAppColors
import androidx.compose.foundation.text.ClickableText as ComposeClickableText

@Composable
fun ClickableText(
    text: AnnotatedString,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = LocalAppColors.current.dark,
    fontSize: TextUnit = FontSize.Normal,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign = TextAlign.Unspecified,
    singleLine: Boolean = false,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    ComposeClickableText(
        text = text,
        onClick = onClick,
        modifier = modifier,
        style = TextStyle(
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
            lineHeight = lineHeight
        ),
        overflow = TextOverflow.Ellipsis,
        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
    )
}