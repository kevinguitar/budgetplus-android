package com.kevlina.budgetplus.feature.welcome.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.feature.welcome.WelcomeViewModel

@Composable
fun WelcomeBinding(vm: WelcomeViewModel) {

    BackHandler(onBack = vm::logout)

    AdaptiveScreen(
        modifier = Modifier.fillMaxSize(),
        regularContent = {
            WelcomeContentRegular(vm)
        },
        wideContent = {
            WelcomeContentWide(vm)
        },
        packedContent = {
            WelcomeContentPacked(vm)
        }
    )
}