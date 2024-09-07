package com.kevlina.budgetplus.app.book.ui

import android.os.Bundle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.getSerializableCompat
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.ARG_AUTHOR_ID
import com.kevlina.budgetplus.core.common.nav.ARG_CATEGORY
import com.kevlina.budgetplus.core.common.nav.ARG_TYPE
import com.kevlina.budgetplus.core.common.nav.BookTab
import com.kevlina.budgetplus.core.common.nav.HistoryDest
import com.kevlina.budgetplus.core.common.nav.navRoute
import com.kevlina.budgetplus.core.common.nav.originalNavValue
import com.kevlina.budgetplus.core.common.nav.toNavigator
import com.kevlina.budgetplus.feature.overview.ui.OverviewScreen
import com.kevlina.budgetplus.feature.records.RecordsScreen
import com.kevlina.budgetplus.feature.records.RecordsViewModel

internal fun NavGraphBuilder.overviewTabGraph(navController: NavController) {

    navigation(
        startDestination = HistoryDest.Overview.navRoute,
        route = BookTab.History.navRoute
    ) {

        composable(
            route = HistoryDest.Overview.navRoute,
            deepLinks = listOf(
                navDeepLink { uriPattern = "$APP_DEEPLINK/${HistoryDest.Overview.navRoute}" }
            )
        ) {
            OverviewScreen(navigator = navController.toNavigator())
        }

        composable(
            route = "${HistoryDest.Records.navRoute}/{$ARG_TYPE}/{$ARG_CATEGORY}?$ARG_AUTHOR_ID={$ARG_AUTHOR_ID}",
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
                vm = hiltViewModel<RecordsViewModel, RecordsViewModel.Factory>(creationCallback = { factory ->
                    factory.create(
                        type = requireNotNull(args.getSerializableCompat(ARG_TYPE)),
                        category = requireNotNull(args.getString(ARG_CATEGORY)).originalNavValue,
                        authorId = args.getString(ARG_AUTHOR_ID)
                    )
                })
            )
        }
    }
}