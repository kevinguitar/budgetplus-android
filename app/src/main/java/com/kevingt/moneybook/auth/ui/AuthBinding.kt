package com.kevingt.moneybook.auth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.kevingt.moneybook.auth.AuthViewModel

@Composable
fun AuthBinding(viewModel: AuthViewModel) {

    Column {

        Button(onClick = { viewModel.loginWithGoogle() }) {
            Text(text = "Google")
        }

        Button(onClick = { viewModel.loginWithFacebook() }) {
            Text(text = "Facebook")
        }
    }
}