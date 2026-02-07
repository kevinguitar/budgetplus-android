package com.kevlina.budgetplus.feature.welcome.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.kevlina.budgetplus.core.common.SnackbarData
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.core.ui.Scaffold
import com.kevlina.budgetplus.core.ui.SnackbarHost
import com.kevlina.budgetplus.feature.welcome.WelcomeViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun WelcomeBinding(vm: WelcomeViewModel) {
    var snackbarData: SnackbarData? by remember { mutableStateOf(null) }

    LaunchedEffect(vm) {
        vm.snackbarSender.snackbarEvent
            .consumeEach { snackbarData = it }
            .collect()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarData) }
    ) {
        NavigationBackHandler(
            state = rememberNavigationEventState(NavigationEventInfo.None),
            onBackCompleted = vm::logout
        )

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
}