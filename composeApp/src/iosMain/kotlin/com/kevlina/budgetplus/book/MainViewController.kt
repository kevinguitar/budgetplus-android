package com.kevlina.budgetplus.book

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.book.ui.BookBinding
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.utils.LocalViewModelGraphProvider
import com.kevlina.budgetplus.core.utils.metroViewModel
import com.kevlina.budgetplus.feature.auth.IosAuthViewModel
import com.kevlina.budgetplus.feature.auth.ui.AuthBinding
import com.kevlina.budgetplus.feature.welcome.WelcomeViewModel
import com.kevlina.budgetplus.feature.welcome.ui.WelcomeBinding
import kotlinx.coroutines.flow.collect
import platform.UIKit.UIViewController

fun MainViewController(deeplink: String?): UIViewController = ComposeUIViewController {
    val graph = remember { BudgetPlusIosAppGraphHolder.graph }
    val themeColors by graph.themeManager.themeColors.collectAsStateWithLifecycle()

    LaunchedEffect(graph) {
        graph.destinationState.value = when {
            graph.authManager.userState.value == null -> Destination.Auth
            graph.bookRepo.currentBookId == null -> Destination.Welcome
            else -> Destination.Book
        }
    }

    LaunchedEffect(graph.navigation) {
        graph.navigation
            .consumeEach { it.navigate() }
            .collect()
    }

    CompositionLocalProvider(
        LocalViewModelGraphProvider provides graph.viewModelGraphProvider
    ) {
        AppTheme(themeColors) {
            when (graph.destinationState.value) {
                Destination.Auth -> {
                    val vm = metroViewModel<IosAuthViewModel>()
                    AuthBinding(
                        vm = vm.commonAuth,
                        signInWithGoogle = vm::signInWithGoogle,
                        signInWithApple = vm::signInWithApple
                    )
                }

                Destination.Welcome -> {
                    val vm = metroViewModel<WelcomeViewModel>()
                    WelcomeBinding(vm = vm)
                }

                Destination.Book -> {
                    val vm = metroViewModel<BookViewModel>()
                    LaunchedEffect(deeplink) {
                        vm.handleDeeplink(deeplink)
                    }

                    BookBinding(vm = vm)
                }
            }
        }
    }
}
