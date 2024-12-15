package com.kevlina.budgetplus.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme

@Composable
fun AuthContentWide(
    signInWithGoogle: () -> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.primary)
            .padding(horizontal = 32.dp)
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1F)
        ) {
            AuthLogo(modifier = Modifier.fillMaxWidth())
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1F)
        ) {

            AuthTitle()

            AuthDescription(modifier = Modifier.padding(all = 16.dp))

            SocialSignInButton(
                onClick = signInWithGoogle,
                textRes = R.string.auth_google,
                iconRes = R.drawable.ic_google
            )
        }
    }
}

@Preview(widthDp = 800, heightDp = 600)
@Composable
private fun AuthContentWide_Preview() = AppTheme {
    AuthContentWide(signInWithGoogle = {})
}