package com.kevlina.budgetplus.book

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.kevlina.budgetplus.book.bubble.Bubble
import com.kevlina.budgetplus.book.category.EditCategoryScreen
import com.kevlina.budgetplus.book.details.DetailsScreen
import com.kevlina.budgetplus.book.details.detailsVm
import com.kevlina.budgetplus.book.overview.OverviewScreen
import com.kevlina.budgetplus.book.premium.PremiumScreen
import com.kevlina.budgetplus.book.record.RecordScreen
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.utils.ARG_CATEGORY
import com.kevlina.budgetplus.utils.ARG_TYPE
import com.kevlina.budgetplus.utils.consume
import com.kevlina.budgetplus.utils.consumeEach
import com.kevlina.budgetplus.utils.getSerializableCompat
import com.kevlina.budgetplus.utils.rippleClick
import com.kevlina.budgetplus.utils.toNavigator
import kotlinx.coroutines.flow.launchIn

@Composable
fun BookBinding(viewModel: BookViewModel) {

    val navController = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consume(context)
            .launchIn(this)

        viewModel.unlockPremiumEvent
            .consumeEach { navController.navigate(AddDest.UnlockPremium.route) }
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
            RecordScreen(navigator = navController.toNavigator())
        }

        composable(
            route = "${AddDest.EditCategory.route}/{$ARG_TYPE}",
            arguments = listOf(navArgument(ARG_TYPE) {
                type = NavType.EnumType(RecordType::class.java)
            })
        ) { entry ->
            val args = entry.arguments ?: Bundle.EMPTY
            EditCategoryScreen(
                navigator = navController.toNavigator(),
                type = args.getSerializableCompat(ARG_TYPE)
            )
        }

        composable(route = AddDest.UnlockPremium.route) {
            PremiumScreen(navigator = navController.toNavigator())
        }
    }
}

fun NavGraphBuilder.overviewTabGraph(navController: NavController) {
    navigation(startDestination = HistoryDest.Overview.route, route = BookTab.History.route) {

        composable(HistoryDest.Overview.route) {
            OverviewScreen(navigator = navController.toNavigator())
        }

        composable(
            route = "${HistoryDest.Details.route}/{$ARG_TYPE}/{$ARG_CATEGORY}",
            arguments = listOf(
                navArgument(ARG_TYPE) { type = NavType.EnumType(RecordType::class.java) },
                navArgument(ARG_CATEGORY) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments ?: Bundle.EMPTY
            DetailsScreen(
                navigator = navController.toNavigator(),
                vm = detailsVm(
                    type = requireNotNull(args.getSerializableCompat(ARG_TYPE)),
                    category = requireNotNull(args.getString(ARG_CATEGORY))
                )
            )
        }
    }
}

@Composable
private fun BottomNav(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = LocalAppColors.current.light)
    ) {

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .alpha(0.4F)
                .background(color = LocalAppColors.current.dark)
        )

        Row(
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
        ) {

            BookTab.values().forEach { tab ->
                BottomNavItem(
                    navController = navController,
                    tab = tab,
                    isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun RowScope.BottomNavItem(
    navController: NavController,
    tab: BookTab,
    isSelected: Boolean,
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
                    .size(width = 60.dp, height = 36.dp)
                    .background(
                        color = LocalAppColors.current.dark,
                        shape = CircleShape
                    )
            )
        }

        Icon(
            imageVector = when (tab) {
                BookTab.Add -> Icons.Rounded.PostAdd
                BookTab.History -> Icons.Rounded.FormatListBulleted
            },
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