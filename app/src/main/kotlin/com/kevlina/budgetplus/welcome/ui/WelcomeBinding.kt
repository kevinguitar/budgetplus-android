package com.kevlina.budgetplus.welcome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kevlina.budgetplus.ui.AdaptiveScreen
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.utils.consume
import com.kevlina.budgetplus.welcome.WelcomeViewModel
import kotlinx.coroutines.flow.launchIn

@Composable
fun WelcomeBinding(viewModel: WelcomeViewModel) {

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consume(context)
            .launchIn(this)
    }

    AdaptiveScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.light),
        regularContent = {
            WelcomeContentRegular(viewModel)
        },
        wideContent = {
            WelcomeContentWide(viewModel)
        },
        packedContent = {
            WelcomeContentPacked(viewModel)
        }
    )
}