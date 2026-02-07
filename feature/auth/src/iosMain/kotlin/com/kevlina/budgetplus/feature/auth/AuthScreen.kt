package com.kevlina.budgetplus.feature.auth

import androidx.compose.runtime.Composable
import com.kevlina.budgetplus.core.utils.metroViewModel
import com.kevlina.budgetplus.feature.auth.ui.AuthBinding

@Composable
fun AuthScreen() {
    val vm = metroViewModel<IosAuthViewModel>()
    AuthBinding(
        vm = vm.commonAuth,
        signInWithGoogle = vm::signInWithGoogle,
        signInWithApple = vm::signInWithApple
    )
}