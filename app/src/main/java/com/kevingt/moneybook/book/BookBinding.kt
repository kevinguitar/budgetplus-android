package com.kevingt.moneybook.book

import android.os.Bundle
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kevingt.moneybook.book.category.EditCategoryScreen
import com.kevingt.moneybook.book.details.DetailsScreen
import com.kevingt.moneybook.book.overview.OverviewScreen
import com.kevingt.moneybook.book.overview.vm.OverviewViewModel
import com.kevingt.moneybook.book.record.RecordScreen
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.utils.ARG_CATEGORY
import com.kevingt.moneybook.utils.ARG_EDIT_RECORD
import com.kevingt.moneybook.utils.ARG_TYPE

@Composable
fun BookBinding(viewModel: BookViewModel) {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNav(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BookTab.Add.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            addTabGraph(navController)
            overviewTabGraph(navController)
        }
    }
}

fun NavGraphBuilder.addTabGraph(navController: NavController) {
    navigation(startDestination = AddDest.Record.route, route = BookTab.Add.route) {

        composable(route = AddDest.Record.route) {
            RecordScreen(
                navController = navController,
                record = navController.previousBackStackEntry
                    ?.arguments?.getParcelable(ARG_EDIT_RECORD)
            )
        }

        composable(
            route = "${AddDest.EditCategory.route}/{$ARG_TYPE}",
            arguments = listOf(navArgument(ARG_TYPE) {
                type = NavType.EnumType(RecordType::class.java)
            })
        ) { entry ->
            val args = entry.arguments ?: Bundle.EMPTY
            EditCategoryScreen(
                navController = navController,
                type = args.getSerializable(ARG_TYPE) as RecordType
            )
        }
    }
}

fun NavGraphBuilder.overviewTabGraph(navController: NavController) {
    navigation(startDestination = HistoryDest.Overview.route, route = BookTab.History.route) {
        composable(HistoryDest.Overview.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(BookTab.History.route)
            }
            val viewModel = hiltViewModel<OverviewViewModel>(parentEntry)
            OverviewScreen(navController = navController, viewModel)
        }
        composable(
            route = "${HistoryDest.Details.route}/{$ARG_CATEGORY}",
            arguments = listOf(navArgument(ARG_CATEGORY) { type = NavType.StringType })
        ) { backStackEntry ->
            val args = backStackEntry.arguments ?: Bundle.EMPTY
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(BookTab.History.route)
            }
            val viewModel = hiltViewModel<OverviewViewModel>(parentEntry)
            DetailsScreen(
                navController = navController,
                viewModel = viewModel,
                category = args.getString(ARG_CATEGORY)
            )
        }
    }
}

@Composable
fun BottomNav(navController: NavController) {

    BottomNavigation(modifier = Modifier.height(48.dp)) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        BookTab.values().forEach { tab ->

            BottomNavigationItem(
                selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                icon = { Icon(tab.icon, contentDescription = null) },
                onClick = {
                    navController.navigate(tab.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}