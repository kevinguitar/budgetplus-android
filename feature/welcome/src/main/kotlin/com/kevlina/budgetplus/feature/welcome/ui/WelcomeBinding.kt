package com.kevlina.budgetplus.feature.welcome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kevlina.budgetplus.core.common.nav.consumeAsEffect
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.feature.welcome.WelcomeViewModel

@Composable
fun WelcomeBinding(vm: WelcomeViewModel) {

    vm.navigation.consumeAsEffect()

    AdaptiveScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.light),
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