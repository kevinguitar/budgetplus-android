package com.kevlina.budgetplus.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.containerPadding

@Composable
fun AuthContent(
    signInWithGoogle: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.primary)
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .containerPadding()
                .padding(vertical = 32.dp)
        ) {

            AuthTitle(
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            AuthLogo(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            AuthDescription(
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(24.dp))

            SocialSignInButton(
                onClick = signInWithGoogle,
                textRes = R.string.auth_google,
                iconRes = R.drawable.ic_google
            )
        }
    }
}

@Preview
@Composable
private fun AuthContent_Preview() = AppTheme {
    AuthContent(signInWithGoogle = {})
}