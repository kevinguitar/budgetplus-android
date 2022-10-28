package com.kevlina.budgetplus.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kevlina.budgetplus.core.common.consume
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.feature.auth.AuthViewModel
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