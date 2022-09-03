package com.kevlina.budgetplus.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kevlina.budgetplus.auth.AuthViewModel
import com.kevlina.budgetplus.ui.AdaptiveScreen
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.utils.consume
import kotlinx.coroutines.flow.launchIn

@Composable
fun AuthBinding(viewModel: AuthViewModel) {

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consume(context)
            .launchIn(this)
    }

    AdaptiveScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.primary),
        regularContent = {
            AuthContent(viewModel)
        },
        wideContent = {
            AuthContentWide(viewModel)
        }
    )
}