package com.kevlina.budgetplus.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kevlina.budgetplus.core.common.SnackbarData
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.core.ui.Scaffold
import com.kevlina.budgetplus.core.ui.SnackbarHost
import com.kevlina.budgetplus.feature.auth.AuthViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun AuthBinding(vm: AuthViewModel) {
    var snackbarData: SnackbarData? by remember { mutableStateOf(null) }

    LaunchedEffect(vm) {
        vm.snackbarSender.snackbarEvent
            .consumeEach { snackbarData = it }
            .collect()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarData) }
    ) {
        AdaptiveScreen(
            modifier = Modifier
                    .fillMaxSize()
                    .background(LocalAppColors.current.primary)
                    .systemBarsPadding(),
            regularContent = {
                AuthContent(
                    signInWithGoogle = vm::signInWithGoogle,
                )
            },
            wideContent = {
                AuthContentWide(
                    signInWithGoogle = vm::signInWithGoogle,
                )
            }
        )
    }
}