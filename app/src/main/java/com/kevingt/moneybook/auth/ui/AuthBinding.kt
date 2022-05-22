package com.kevingt.moneybook.auth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.kevingt.moneybook.auth.AuthViewModel
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

    Column {
        Button(onClick = { viewModel.signInWithFacebook() }) {
            Text(text = "Facebook")
        }

        Button(onClick = { viewModel.signInWithGoogle() }) {
            Text(text = "Google")
        }
    }
}