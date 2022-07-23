package com.kevingt.moneybook.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kevingt.moneybook.R
import com.kevingt.moneybook.auth.AuthViewModel
import com.kevingt.moneybook.ui.AppButton
import com.kevingt.moneybook.ui.AppText
import com.kevingt.moneybook.ui.LocalAppColors
import com.kevingt.moneybook.utils.consumeEach
import kotlinx.coroutines.flow.launchIn

@Composable
fun AuthBinding(viewModel: AuthViewModel) {

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consumeEach(context)
            .launchIn(this)
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppButton(onClick = { viewModel.signInWithFacebook() }) {
            AppText(
                text = stringResource(id = R.string.auth_facebook),
                color = LocalAppColors.current.light
            )
        }

        AppButton(onClick = { viewModel.signInWithGoogle() }) {
            AppText(
                text = stringResource(id = R.string.auth_google),
                color = LocalAppColors.current.light
            )
        }
    }
}