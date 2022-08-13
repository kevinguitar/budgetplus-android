package com.kevlina.budgetplus.welcome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.utils.consumeEach
import com.kevlina.budgetplus.welcome.WelcomeViewModel
import kotlinx.coroutines.flow.launchIn

@Composable
fun WelcomeBinding(viewModel: WelcomeViewModel) {

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consumeEach(context)
            .launchIn(this)
    }

    WelcomeContent(viewModel)
}

@Composable
private fun WelcomeContent(viewModel: WelcomeViewModel) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.light)
    ) {

        CreateBookBlock(createBook = viewModel::createBook)

        CollabBlock()
    }
}