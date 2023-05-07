package com.kevlina.budgetplus.book.ui

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kevlina.budgetplus.core.common.nav.BookTab
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.rippleClick

@Composable
internal fun BottomNav(navController: NavController) {

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
                .alpha(AppTheme.DIVIDER_ALPHA)
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
            tint = if (isSelected) {
                LocalAppColors.current.light
            } else {
                LocalAppColors.current.dark
            },
            modifier = Modifier.size(28.dp)
        )
    }
}