package com.kevlina.budgetplus.app.book.ui

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.BookTab
import com.kevlina.budgetplus.core.common.nav.HistoryDest
import com.kevlina.budgetplus.core.common.nav.NAV_OVERVIEW_PATH
import com.kevlina.budgetplus.feature.overview.ui.OverviewScreen
import com.kevlina.budgetplus.feature.records.RecordsScreen
import com.kevlina.budgetplus.feature.records.RecordsViewModel
import com.kevlina.budgetplus.feature.search.SearchScreen
import com.kevlina.budgetplus.feature.search.SearchViewModel

internal fun NavGraphBuilder.overviewTabGraph(navController: NavController) {

    navigation<BookTab.History>(
        startDestination = HistoryDest.Overview,
    ) {

        composable<HistoryDest.Overview>(
            deepLinks = listOf(
                navDeepLink<HistoryDest.Overview>(basePath = "$APP_DEEPLINK/$NAV_OVERVIEW_PATH")
            )
        ) {
            OverviewScreen(navController)
        }

        composable<HistoryDest.Records> { entry ->
            RecordsScreen(
                navController = navController,
                vm = hiltViewModel<RecordsViewModel, RecordsViewModel.Factory>(creationCallback = { factory ->
                    factory.create(entry.toRoute())
                })
            )
        }

        composable<HistoryDest.Search> { entry ->
            SearchScreen(
                navController = navController,
                vm = hiltViewModel<SearchViewModel, SearchViewModel.Factory>(creationCallback = { factory ->
                    factory.create(entry.toRoute())
                })
            )
        }
    }
}