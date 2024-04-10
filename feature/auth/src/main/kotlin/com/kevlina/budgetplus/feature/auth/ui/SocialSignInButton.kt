package com.kevlina.budgetplus.feature.auth.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Text

@Composable
fun SocialSignInButton(
    onClick: () -> Unit,
    @StringRes textRes: Int,
    @DrawableRes iconRes: Int,
) {

    Button(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
            .height(56.dp),
        onClick = onClick,
        color = LocalAppColors.current.light
    ) {

        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp)
        )

        Text(
            text = stringResource(id = textRes),
            color = LocalAppColors.current.dark,
            fontSize = FontSize.SemiLarge,
            fontWeight = FontWeight.Medium,
        )
    }
}

@PreviewFontScale
@Composable
private fun SocialSignInButton_Preview() = AppTheme {
    SocialSignInButton(
        onClick = { },
        textRes = R.string.auth_google,
        iconRes = R.drawable.ic_google
    )
}