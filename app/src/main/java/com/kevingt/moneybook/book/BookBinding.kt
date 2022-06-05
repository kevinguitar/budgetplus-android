package com.kevingt.moneybook.book

import android.os.Bundle
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
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
import com.kevingt.moneybook.utils.consumeEach
import kotlinx.coroutines.flow.launchIn

@Composable
fun BookBinding(viewModel: BookViewModel) {

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consumeEach(context)
            .launchIn(this)
    }

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
        composable(AddDest.Record.route) { RecordScreen(navController = navController) }
        composable(
            route = "${AddDest.EditCategory.route}?type={type}",
            arguments = listOf(navArgument("type") {
                type = NavType.EnumType(RecordType::class.java)
            })
        ) { entry ->
            val args = entry.arguments ?: Bundle.EMPTY
            EditCategoryScreen(
                navController = navController,
                type = args.getSerializable("type") as RecordType
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
            route = "${HistoryDest.Details.route}?category={category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val args = backStackEntry.arguments ?: Bundle.EMPTY
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(BookTab.History.route)
            }
            val viewModel = hiltViewModel<OverviewViewModel>(parentEntry)
            DetailsScreen(
                navController = navController,
                viewModel = viewModel,
                category = args.getString("category")
            )
        }
    }
}

@Composable
fun BottomNav(navController: NavController) {

    BottomNavigation(modifier = Modifier.height(48.dp)) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentHierarchy = navBackStackEntry?.destination?.hierarchy

        BottomNavigationItem(
            selected = currentHierarchy?.any { it.route == BookTab.Add.route } == true,
            onClick = { navController.navigate(BookTab.Add.route) },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
        )

        BottomNavigationItem(
            selected = currentHierarchy?.any { it.route == BookTab.History.route } == true,
            onClick = { navController.navigate(BookTab.History.route) },
            icon = { Icon(Icons.Filled.List, contentDescription = null) },
        )
    }
}