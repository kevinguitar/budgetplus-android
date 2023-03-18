package com.kevlina.budgetplus.core.common.nav

import androidx.compose.runtime.Stable
import androidx.navigation.NavController

/**
 *  Internal version of NavController, mark it as stable to improve the composable performance.
 */
@Stable
class Navigator(
    private val doNavigate: (String) -> Unit,
    private val doUp: () -> Boolean
) {

    fun navigate(route: String) = doNavigate(route)
    fun navigateUp(): Boolean = doUp()
}

fun NavController.toNavigator() = Navigator(
    doNavigate = ::navigate,
    doUp = ::navigateUp
)