package com.kevlina.budgetplus.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.auth.AuthViewModel

@Composable
fun AuthContentWide(viewModel: AuthViewModel) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1F)
        ) {
            AnimatedLogo()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1F)
        ) {

            AuthTitle()

            AuthDescription(modifier = Modifier.padding(vertical = 16.dp))

            SocialSignInButton(
                onClick = viewModel::signInWithGoogle,
                textRes = R.string.auth_google,
                iconRes = R.drawable.ic_google
            )

            SocialSignInButton(
                onClick = viewModel::signInWithFacebook,
                textRes = R.string.auth_facebook,
                iconRes = R.drawable.ic_facebook
            )
        }
    }
}