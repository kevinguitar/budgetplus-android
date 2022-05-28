package com.kevingt.moneybook.book

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kevingt.moneybook.book.overview.OverviewScreen
import com.kevingt.moneybook.book.record.RecordScreen
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
            startDestination = BookTab.Record.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BookTab.Record.route) { RecordScreen(navController) }
            composable(BookTab.Overview.route) { OverviewScreen() }
        }
    }
}

@Composable
fun BottomNav(navController: NavController) {

    BottomNavigation {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentHierarchy = navBackStackEntry?.destination?.hierarchy

        BottomNavigationItem(
            selected = currentHierarchy?.any { it.route == BookTab.Record.route } == true,
            onClick = { navController.navigate(BookTab.Record.route) },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            label = { Text(text = "Record") }
        )

        BottomNavigationItem(
            selected = currentHierarchy?.any { it.route == BookTab.Overview.route } == true,
            onClick = { navController.navigate(BookTab.Overview.route) },
            icon = { Icon(Icons.Filled.List, contentDescription = null) },
            label = { Text(text = "Overview") }
        )
    }
}