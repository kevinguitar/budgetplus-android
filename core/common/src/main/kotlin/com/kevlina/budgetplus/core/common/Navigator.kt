package com.kevlina.budgetplus.core.common

import androidx.compose.runtime.Stable
import androidx.navigation.NavController

/**
 *  Internal version of NavController, mark it as stable to improve the composable performance.
 */
@Stable
class Navigator(
    private val _navigate: (String) -> Unit,
    private val _up: () -> Boolean
) {

    fun navigate(route: String) = _navigate(route)
    fun navigateUp(): Boolean = _up()
}

fun NavController.toNavigator() = Navigator(
    _navigate = ::navigate,
    _up = ::navigateUp
)