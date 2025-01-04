package com.kevlina.budgetplus.app.book.ui

import android.content.Intent
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kevlina.budgetplus.app.book.BookViewModel
import com.kevlina.budgetplus.core.common.SnackbarData
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.common.nav.BookTab
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.Scaffold
import com.kevlina.budgetplus.core.ui.SnackbarHost
import com.kevlina.budgetplus.core.ui.bubble.Bubble
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@Composable
internal fun BookBinding(
    vm: BookViewModel,
    newIntent: Intent?,
) {

    val navController = rememberNavController()

    val showAds by vm.showAds.collectAsStateWithLifecycle()
    val isAdMobInitialized by vm.isAdMobInitialized.collectAsStateWithLifecycle()
    val previewColors by vm.themeManager.previewColors.collectAsStateWithLifecycle()
    val bubbleDest by vm.bubbleViewModel.destination.collectAsStateWithLifecycle()

    var snackbarData: SnackbarData? by remember { mutableStateOf(null) }
    var isUnlockingPremium by remember { mutableStateOf(false) }

    LaunchedEffect(vm) {
        vm.unlockPremiumEvent
            .consumeEach { navController.navigate(AddDest.UnlockPremium) }
            .launchIn(this)

        vm.snackbarSender.snackbarEvent
            .consumeEach { snackbarData = it }
            .launchIn(this)
    }

    LaunchedEffect(newIntent) {
        try {
            navController.handleDeepLink(newIntent)
        } catch (e: Exception) {
            Timber.e(e, "Fail to handle deeplink")
        }
    }

    // Clear the preview colors if the user navigates out of the picker screen.
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow
            .onEach { entry ->
                if (!entry.destination.hasRoute<AddDest.Colors>()) {
                    vm.themeManager.clearPreviewColors()
                }

                isUnlockingPremium = entry.destination.hasRoute<AddDest.UnlockPremium>()
            }
            .collect()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            bottomBar = { BottomNav(navController, previewColors) },
            snackbarHost = { SnackbarHost(snackbarData) },
        ) { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    // Do not consider the top padding, and let TopBar handle it.
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .background(color = previewColors?.light ?: LocalAppColors.current.light)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = BookTab.Add,
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() },
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxWidth()
                        .background(color = previewColors?.light ?: LocalAppColors.current.light)
                ) {
                    addTabGraph(navController)
                    overviewTabGraph(navController)
                }

                if (showAds && !isUnlockingPremium) {
                    AdsBanner(isAdMobInitialized = isAdMobInitialized)
                }
            }
        }

        Bubble(
            dest = bubbleDest,
            dismissBubble = vm.bubbleViewModel::dismissBubble
        )
    }
}