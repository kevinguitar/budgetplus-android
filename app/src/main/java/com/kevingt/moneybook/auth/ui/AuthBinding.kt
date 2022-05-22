package com.kevingt.moneybook.auth.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.kevingt.moneybook.auth.AuthViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun AuthBinding(viewModel: AuthViewModel) {

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigationFlow
            .onEach { info ->
                context.startActivity(Intent(context, info.destination.java))
                if (info.finishCurrent) {
                    (context as Activity).finish()
                }
            }
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