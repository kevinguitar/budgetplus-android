package com.kevlina.budgetplus.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.ClickableText
import com.kevlina.budgetplus.core.ui.FontSize

private const val HINT_ALPHA = 0.7F

@Composable
internal fun FacebookUserHint(
    modifier: Modifier = Modifier,
    onContactClick: () -> Unit,
) {
    val hint = stringResource(id = R.string.auth_facebook_hint)
    val contact = stringResource(id = R.string.auth_facebook_hint_contact)
    val contactIndex = remember(hint, contact) { hint.indexOf(contact) }

    ClickableText(
        text = buildAnnotatedString {
            append(hint)
            addStyle(
                style = SpanStyle(textDecoration = TextDecoration.Underline),
                start = contactIndex,
                end = contactIndex + contact.length
            )
        },
        onClick = { index ->
            if (index in contactIndex..contactIndex + contact.length) {
                onContactClick()
            }
        },
        modifier = modifier
            .padding(horizontal = 32.dp)
            .alpha(HINT_ALPHA),
        color = LocalAppColors.current.light,
        textAlign = TextAlign.Center,
        fontSize = FontSize.XSmall,
        lineHeight = 16.sp
    )
}

@Preview
@Composable
private fun FacebookUserHint_Preview() = AppTheme {
    FacebookUserHint(
        modifier = Modifier.background(LocalAppColors.current.primary),
        onContactClick = {}
    )
}