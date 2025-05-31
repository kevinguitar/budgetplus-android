package com.kevlina.budgetplus.app.book.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.common.nav.BookTab
import com.kevlina.budgetplus.core.common.nav.NAV_COLORS_PATH
import com.kevlina.budgetplus.core.common.nav.NAV_RECORD_PATH
import com.kevlina.budgetplus.core.common.nav.NAV_SETTINGS_PATH
import com.kevlina.budgetplus.core.common.nav.NAV_UNLOCK_PREMIUM_PATH
import com.kevlina.budgetplus.feature.add.record.ui.RecordScreen
import com.kevlina.budgetplus.feature.batch.record.ui.BatchRecordScreen
import com.kevlina.budgetplus.feature.color.tone.picker.ColorTonePickerScreen
import com.kevlina.budgetplus.feature.currency.picker.CurrencyPickerScreen
import com.kevlina.budgetplus.feature.edit.category.EditCategoryScreen
import com.kevlina.budgetplus.feature.settings.SettingsScreen
import com.kevlina.budgetplus.feature.unlock.premium.PremiumScreen

internal fun NavGraphBuilder.addTabGraph(navController: NavController) {

    navigation<BookTab.Add>(
        startDestination = AddDest.Record,
    ) {

        composable<AddDest.Record>(
            deepLinks = listOf(
                navDeepLink<AddDest.Record>(basePath = "$APP_DEEPLINK/$NAV_RECORD_PATH")
            )
        ) {
            RecordScreen(navController)
        }

        composable<AddDest.EditCategory> { entry ->
            EditCategoryScreen(
                navController = navController,
                type = entry.toRoute<AddDest.EditCategory>().type
            )
        }

        composable<AddDest.Settings>(
            deepLinks = listOf(
                navDeepLink<AddDest.Settings>(basePath = "$APP_DEEPLINK/$NAV_SETTINGS_PATH")
            )
        ) { entry ->
            SettingsScreen(
                navController = navController,
                showMembers = entry.toRoute<AddDest.Settings>().showMembers
            )
        }

        composable<AddDest.UnlockPremium>(
            deepLinks = listOf(
                navDeepLink<AddDest.UnlockPremium>(basePath = "$APP_DEEPLINK/$NAV_UNLOCK_PREMIUM_PATH")
            )
        ) {
            PremiumScreen(navController)
        }

        composable<AddDest.BatchRecord> {
            BatchRecordScreen(navController)
        }

        composable<AddDest.Colors>(
            deepLinks = listOf(
                navDeepLink<AddDest.Colors>(basePath = "$APP_DEEPLINK/$NAV_COLORS_PATH")
            )
        ) { entry ->
            ColorTonePickerScreen(
                navController = navController,
                hexFromLink = entry.toRoute<AddDest.Colors>().hex
            )
        }

        composable<AddDest.CurrencyPicker> {
            CurrencyPickerScreen(navController)
        }
    }
}