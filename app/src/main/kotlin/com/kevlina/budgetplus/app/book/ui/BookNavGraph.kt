package com.kevlina.budgetplus.app.book.ui

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import com.kevlina.budgetplus.core.common.nav.BookDest
import com.kevlina.budgetplus.core.common.nav.NavController
import com.kevlina.budgetplus.feature.add.record.ui.RecordScreen
import com.kevlina.budgetplus.feature.batch.record.ui.BatchRecordScreen
import com.kevlina.budgetplus.feature.color.tone.picker.ColorTonePickerScreen
import com.kevlina.budgetplus.feature.currency.picker.CurrencyPickerScreen
import com.kevlina.budgetplus.feature.edit.category.EditCategoryScreen
import com.kevlina.budgetplus.feature.overview.ui.OverviewScreen
import com.kevlina.budgetplus.feature.records.RecordsScreen
import com.kevlina.budgetplus.feature.records.RecordsViewModel
import com.kevlina.budgetplus.feature.search.SearchScreen
import com.kevlina.budgetplus.feature.search.SearchViewModel
import com.kevlina.budgetplus.feature.settings.SettingsScreen
import com.kevlina.budgetplus.feature.unlock.premium.PremiumScreen

internal fun bookNavGraph(
    navController: NavController<BookDest>,
    bookDest: BookDest,
): NavEntry<BookDest> {
    return when (bookDest) {
        BookDest.Record -> NavEntry(bookDest) {
            RecordScreen(navController)
        }

        is BookDest.EditCategory -> NavEntry(bookDest) {
            EditCategoryScreen(
                navController = navController,
                type = bookDest.type
            )
        }

        is BookDest.Settings -> NavEntry(bookDest) {
            SettingsScreen(
                navController = navController,
                showMembers = bookDest.showMembers
            )
        }

        BookDest.UnlockPremium -> NavEntry(bookDest) {
            PremiumScreen(navController)
        }

        BookDest.BatchRecord -> NavEntry(bookDest) {
            BatchRecordScreen(navController)
        }

        is BookDest.Colors -> NavEntry(bookDest) {
            ColorTonePickerScreen(
                navController = navController,
                hexFromLink = bookDest.hex
            )
        }

        BookDest.CurrencyPicker -> NavEntry(bookDest) {
            CurrencyPickerScreen(navController)
        }

        BookDest.Overview -> NavEntry(bookDest) {
            OverviewScreen(navController)
        }

        is BookDest.Records -> NavEntry(bookDest) {
            RecordsScreen(
                navController = navController,
                vm = hiltViewModel<RecordsViewModel, RecordsViewModel.Factory>(
                    creationCallback = { factory -> factory.create(bookDest) }
                )
            )
        }

        is BookDest.Search -> NavEntry(bookDest) {
            SearchScreen(
                navController = navController,
                vm = hiltViewModel<SearchViewModel, SearchViewModel.Factory>(
                    creationCallback = { factory -> factory.create(bookDest) }
                )
            )
        }
    }

    //TODO: Hook deeplink
    /*
        composable<BookDest.Record>(
            deepLinks = listOf(
                navDeepLink<BookDest.Record>(basePath = "$APP_DEEPLINK/$NAV_RECORD_PATH")
            )
        )

        composable<BookDest.Settings>(
            deepLinks = listOf(
                navDeepLink<BookDest.Settings>(basePath = "$APP_DEEPLINK/$NAV_SETTINGS_PATH")
            )
        )

        composable<BookDest.UnlockPremium>(
            deepLinks = listOf(
                navDeepLink<BookDest.UnlockPremium>(basePath = "$APP_DEEPLINK/$NAV_UNLOCK_PREMIUM_PATH")
            )
        )

        composable<BookDest.Colors>(
            deepLinks = listOf(
                navDeepLink<BookDest.Colors>(basePath = "$APP_DEEPLINK/$NAV_COLORS_PATH")
            )
        )

        composable<HistoryDest.Overview>(
            deepLinks = listOf(
                navDeepLink<HistoryDest.Overview>(basePath = "$APP_DEEPLINK/$NAV_OVERVIEW_PATH")
            )
        )
    }*/
}