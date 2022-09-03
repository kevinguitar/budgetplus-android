package com.kevlina.budgetplus.auth.ui

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.ui.AppButton
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.FontSize
import com.kevlina.budgetplus.ui.LocalAppColors

@Composable
fun SocialSignInButton(
    onClick: () -> Unit,
    @StringRes textRes: Int,
    @DrawableRes iconRes: Int,
) {

    AppButton(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp),
        onClick = onClick,
        color = LocalAppColors.current.light
    ) {

        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 16.dp)
        )

        AppText(
            text = stringResource(id = textRes),
            color = LocalAppColors.current.dark,
            fontSize = FontSize.SemiLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1F)
        )
    }
}