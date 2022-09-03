package com.kevlina.budgetplus.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.FontSize
import com.kevlina.budgetplus.ui.LocalAppColors

@Composable
fun AuthTitle(modifier: Modifier = Modifier) {

    AppText(
        text = stringResource(id = R.string.auth_welcome_title),
        color = LocalAppColors.current.light,
        fontSize = FontSize.HeaderXLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

@Composable
fun AuthDescription(modifier: Modifier) {

    AppText(
        text = stringResource(id = R.string.auth_welcome_description),
        color = LocalAppColors.current.light,
        modifier = modifier
    )
}