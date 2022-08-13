package com.kevlina.budgetplus.auth.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.auth.AuthViewModel
import com.kevlina.budgetplus.ui.*
import com.kevlina.budgetplus.utils.consumeEach
import kotlinx.coroutines.flow.launchIn

@Composable
fun AuthBinding(viewModel: AuthViewModel) {

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consumeEach(context)
            .launchIn(this)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.primary)
    ) {

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

            CurvedAuthTitle()

            Box(contentAlignment = Alignment.Center) {

                Image(
                    painter = painterResource(id = R.drawable.ic_logo_bg),
                    contentDescription = null
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = null
                )
            }

            AppText(
                text = stringResource(id = R.string.auth_welcome_description),
                color = LocalAppColors.current.light,
                modifier = Modifier.padding(vertical = 32.dp)
            )

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

@Composable
private fun SocialSignInButton(
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