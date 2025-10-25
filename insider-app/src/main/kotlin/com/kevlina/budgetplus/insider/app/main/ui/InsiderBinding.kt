package com.kevlina.budgetplus.insider.app.main.ui

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.kevlina.budgetplus.core.common.SnackbarData
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.nav.InsiderDest
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.Scaffold
import com.kevlina.budgetplus.core.ui.SnackbarHost
import com.kevlina.budgetplus.feature.insider.InsiderScreen
import com.kevlina.budgetplus.feature.push.notifications.PushNotificationsScreen
import com.kevlina.budgetplus.insider.app.main.InsiderViewModel
import kotlinx.coroutines.flow.launchIn

@Composable
internal fun InsiderBinding(vm: InsiderViewModel) {

    var snackbarData: SnackbarData? by remember { mutableStateOf(null) }

    LaunchedEffect(vm) {
        vm.snackbarSender.snackbarEvent
            .consumeEach { snackbarData = it }
            .launchIn(this)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarData) },
        ) { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = LocalAppColors.current.light)
                    // Do not consider the top padding, and let TopBar handle it.
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                val backStack = rememberNavBackStack(InsiderDest.Insider)

                NavDisplay(
                    backStack = backStack,
                    entryProvider = entryProvider {
                        entry<InsiderDest.Insider> {
                            InsiderScreen(openPushNotifications = { backStack.add(InsiderDest.PushNotifications) })
                        }

                        entry<InsiderDest.PushNotifications> {
                            PushNotificationsScreen(navigateUp = { backStack.removeLastOrNull() })
                        }
                    },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    transitionSpec = { ContentTransform(fadeIn(), fadeOut()) },
                    popTransitionSpec = { ContentTransform(fadeIn(), fadeOut()) },
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxWidth()
                        .background(color = LocalAppColors.current.light)
                )
            }
        }
    }
}