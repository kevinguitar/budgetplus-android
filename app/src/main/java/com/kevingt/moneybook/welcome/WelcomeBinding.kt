package com.kevingt.moneybook.welcome

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WelcomeBinding(
    viewModel: WelcomeViewModel = viewModel()
) {

    Text(text = "Welcome screen!")
}