package com.kevlina.budgetplus.feature.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Text

@Composable
fun AuthTitle(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.auth_welcome_title),
        color = LocalAppColors.current.light,
        textAlign = TextAlign.Center,
        fontSize = FontSize.HeaderXLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

@Composable
fun AuthDescription(modifier: Modifier) {
    Text(
        text = stringResource(id = R.string.auth_welcome_description),
        color = LocalAppColors.current.light,
        textAlign = TextAlign.Center,
        fontSize = FontSize.Normal,
        lineHeight = 24.sp,
        modifier = modifier
    )
}