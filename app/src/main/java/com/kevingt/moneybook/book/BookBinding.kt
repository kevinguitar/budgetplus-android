package com.kevingt.moneybook.book

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.kevingt.moneybook.utils.consumeEach
import kotlinx.coroutines.flow.launchIn

@Composable
fun BookBinding(viewModel: BookViewModel) {

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consumeEach(context)
            .launchIn(this)
    }

    Button(onClick = { viewModel.logout() }) {
        Text(text = "Logout")
    }

    /*val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ""
    ) {

    }*/
}