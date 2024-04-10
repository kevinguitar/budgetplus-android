package com.kevlina.budgetplus.feature.auth.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kevlina.budgetplus.core.common.nav.consumeAsEffect
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.feature.auth.AuthViewModel

@Composable
fun AuthBinding(vm: AuthViewModel) {

    vm.navigation.consumeAsEffect()

    AdaptiveScreen(
        modifier = Modifier.fillMaxSize(),
        regularContent = {
            AuthContent(
                signInWithGoogle = vm::signInWithGoogle,
                onContactClick = vm::onContactClick
            )
        },
        wideContent = {
            AuthContentWide(
                signInWithGoogle = vm::signInWithGoogle,
                onContactClick = vm::onContactClick
            )
        }
    )
}