package com.kevlina.budgetplus.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.auth.AuthViewModel
import com.kevlina.budgetplus.ui.AppTheme

@Composable
fun AuthContent(viewModel: AuthViewModel) {

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(AppTheme.maxContentWidth)
                .align(Alignment.Center)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 32.dp)
        ) {

            AuthTitle(modifier = Modifier.padding(bottom = 24.dp))

            AnimatedLogo()

            AuthDescription(modifier = Modifier.padding(vertical = 24.dp))

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