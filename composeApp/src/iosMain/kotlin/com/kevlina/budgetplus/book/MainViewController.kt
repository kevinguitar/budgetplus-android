package com.kevlina.budgetplus.book

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.kevlina.budgetplus.book.ui.BookBinding
import com.kevlina.budgetplus.core.utils.LocalViewModelGraphProvider
import com.kevlina.budgetplus.core.utils.metroViewModel
import platform.UIKit.UIViewController

fun MainViewController(deeplink: String?): UIViewController = ComposeUIViewController {
    val graph = remember { BudgetPlusIosAppGraphHolder.graph }

    CompositionLocalProvider(
        LocalViewModelGraphProvider provides graph.viewModelGraphProvider
    ) {
        val vm = metroViewModel<BookViewModel>()
        LaunchedEffect(deeplink) {
            vm.handleDeeplink(deeplink)
        }

        BookBinding(vm)
    }
}
