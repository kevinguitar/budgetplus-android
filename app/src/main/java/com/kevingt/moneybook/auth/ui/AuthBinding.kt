package com.kevingt.moneybook.auth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun AuthBinding(viewModel: AuthViewModel) {

    Column {
        Button(onClick = { viewModel.signInWithFacebook() }) {
            Text(text = "Facebook")
        }

        Button(onClick = { viewModel.signInWithGoogle() }) {
            Text(text = "Google")
        }
    }
}