package com.kevlina.budgetplus.book

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kevlina.budgetplus.book.bubble.Bubble
import com.kevlina.budgetplus.book.category.EditCategoryScreen
import com.kevlina.budgetplus.book.details.DetailsScreen
import com.kevlina.budgetplus.book.overview.OverviewScreen
import com.kevlina.budgetplus.book.overview.vm.OverviewViewModel
import com.kevlina.budgetplus.book.record.RecordScreen
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.utils.ARG_CATEGORY
import com.kevlina.budgetplus.utils.ARG_TYPE
import com.kevlina.budgetplus.utils.consume
import com.kevlina.budgetplus.utils.rippleClick
import kotlinx.coroutines.flow.launchIn

@Composable
fun BookBinding(viewModel: BookViewModel) {

    val navController = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consume(context)
            .launchIn(this)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            bottomBar = { BottomNav(navController) }
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

fun NavGraphBuilder.addTabGraph(navController: NavController) {
    navigation(startDestination = AddDest.Record.route, route = BookTab.Add.route) {

        composable(route = AddDest.Record.route) {
            RecordScreen(navController = navController)
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
                type = requireNotNull(args.getSerializable(ARG_TYPE, RecordType::class.java))
            )
        }
    }
}

fun NavGraphBuilder.overviewTabGraph(navController: NavController) {
    navigation(startDestination = HistoryDest.Overview.route, route = BookTab.History.route) {

        composable(HistoryDest.Overview.route) {
            OverviewScreen(navController = navController)
        }

        composable(
            route = "${HistoryDest.Details.route}/{$ARG_CATEGORY}",
            arguments = listOf(navArgument(ARG_CATEGORY) { type = NavType.StringType })
        ) { backStackEntry ->
            val args = backStackEntry.arguments ?: Bundle.EMPTY
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HistoryDest.Overview.route)
            }
            // Share the same VM instance with overview screen
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
private fun BottomNav(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = LocalAppColors.current.light)
    ) {

        Row(modifier = Modifier.fillMaxSize()) {

            BookTab.values().forEach { tab ->
                BottomNavItem(
                    navController = navController,
                    tab = tab,
                    isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                )
            }
        }

        Spacer(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(1.dp)
                .alpha(0.4F)
                .background(color = LocalAppColors.current.dark)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun RowScope.BottomNavItem(
    navController: NavController,
    tab: BookTab,
    isSelected: Boolean
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .weight(1F)
            .fillMaxHeight()
            .rippleClick {
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
    ) {

        this@BottomNavItem.AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn(),
            exit = scaleOut()
        ) {

            Spacer(
                modifier = Modifier
                    .size(width = 60.dp, height = 32.dp)
                    .background(
                        color = LocalAppColors.current.dark,
                        shape = CircleShape
                    )
            )
        }

        Icon(
            imageVector = tab.icon,
            contentDescription = null,
            tint = if (isSelected) {
                LocalAppColors.current.light
            } else {
                LocalAppColors.current.dark
            },
            modifier = Modifier.size(28.dp)
        )
    }
}