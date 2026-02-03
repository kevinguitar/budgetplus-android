package com.kevlina.budgetplus.feature.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.auth_google
import budgetplus.core.common.generated.resources.ic_google
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Text
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SocialSignInButton(
    onClick: () -> Unit,
    textRes: StringResource,
    iconRes: DrawableResource,
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
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp)
        )

        Text(
            text = stringResource(textRes),
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
        textRes = Res.string.auth_google,
        iconRes = Res.drawable.ic_google
    )
}