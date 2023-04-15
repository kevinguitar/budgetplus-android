package com.kevlina.budgetplus.book.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.kevlina.budgetplus.book.recordsVm
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.getSerializableCompat
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.ARG_AUTHOR_ID
import com.kevlina.budgetplus.core.common.nav.ARG_CATEGORY
import com.kevlina.budgetplus.core.common.nav.ARG_TYPE
import com.kevlina.budgetplus.core.common.nav.BookTab
import com.kevlina.budgetplus.core.common.nav.HistoryDest
import com.kevlina.budgetplus.core.common.nav.originalNavValue
import com.kevlina.budgetplus.core.common.nav.toNavigator
import com.kevlina.budgetplus.feature.overview.ui.OverviewScreen
import com.kevlina.budgetplus.feature.records.RecordsScreen

internal fun NavGraphBuilder.overviewTabGraph(navController: NavController) {
    navigation(startDestination = HistoryDest.Overview.route, route = BookTab.History.route) {

        composable(
            route = HistoryDest.Overview.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "$APP_DEEPLINK/${HistoryDest.Overview.route}" }
            )
        ) {
            OverviewScreen(navigator = navController.toNavigator())
        }

        composable(
            route = "${HistoryDest.Records.route}/{$ARG_TYPE}/{$ARG_CATEGORY}?$ARG_AUTHOR_ID={$ARG_AUTHOR_ID}",
            arguments = listOf(
                navArgument(ARG_TYPE) { type = NavType.EnumType(RecordType::class.java) },
                navArgument(ARG_CATEGORY) { type = NavType.StringType },
                navArgument(ARG_AUTHOR_ID) {
                    nullable = true
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments ?: Bundle.EMPTY
            RecordsScreen(
                navigator = navController.toNavigator(),
                vm = recordsVm(
                    type = requireNotNull(args.getSerializableCompat(ARG_TYPE)),
                    category = requireNotNull(args.getString(ARG_CATEGORY)).originalNavValue,
                    authorId = args.getString(ARG_AUTHOR_ID)
                )
            )
        }
    }
}