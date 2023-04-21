package com.kevlina.budgetplus.book.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kevlina.budgetplus.book.BookViewModel
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.common.nav.BookTab
import com.kevlina.budgetplus.core.common.nav.consume
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.Scaffold
import com.kevlina.budgetplus.core.ui.SnackbarData
import com.kevlina.budgetplus.core.ui.SnackbarHost
import com.kevlina.budgetplus.core.ui.bubble.Bubble
import kotlinx.coroutines.flow.launchIn

@Composable
internal fun BookBinding(
    viewModel: BookViewModel,
    newIntent: Intent?,
) {

    val navController = rememberNavController()
    val context = LocalContext.current

    var snackbarData: SnackbarData? by remember { mutableStateOf(null) }

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consume(context)
            .launchIn(this)

        viewModel.unlockPremiumEvent
            .consumeEach { navController.navigate(AddDest.UnlockPremium.route) }
            .launchIn(this)

        viewModel.snackbarSender.snackbarEvent
            .consumeEach { snackbarData = it }
            .launchIn(this)
    }

    LaunchedEffect(newIntent) {
        navController.handleDeepLink(newIntent)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            bottomBar = { BottomNav(navController) },
            snackbarHost = { SnackbarHost(snackbarData) },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BookTab.Add.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = LocalAppColors.current.light)
            ) {
                addTabGraph(navController)
                overviewTabGraph(navController)
            }
        }

        Bubble()
    }
}