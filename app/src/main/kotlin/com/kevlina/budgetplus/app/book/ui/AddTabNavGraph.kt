package com.kevlina.budgetplus.app.book.ui

import android.os.Bundle
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
import com.kevlina.budgetplus.core.common.nav.ARG_SHOW_MEMBERS
import com.kevlina.budgetplus.core.common.nav.ARG_TYPE
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.common.nav.BookTab
import com.kevlina.budgetplus.core.common.nav.navRoute
import com.kevlina.budgetplus.core.common.nav.toNavigator
import com.kevlina.budgetplus.core.theme.ThemeManager.Companion.COLORS_LINK_QUERY
import com.kevlina.budgetplus.feature.add.record.ui.RecordScreen
import com.kevlina.budgetplus.feature.batch.record.ui.BatchRecordScreen
import com.kevlina.budgetplus.feature.color.tone.picker.ColorTonePickerScreen
import com.kevlina.budgetplus.feature.currency.picker.CurrencyPickerScreen
import com.kevlina.budgetplus.feature.edit.category.EditCategoryScreen
import com.kevlina.budgetplus.feature.insider.InsiderScreen
import com.kevlina.budgetplus.feature.push.notifications.PushNotificationsScreen
import com.kevlina.budgetplus.feature.settings.SettingsScreen
import com.kevlina.budgetplus.feature.unlock.premium.PremiumScreen

internal fun NavGraphBuilder.addTabGraph(navController: NavController) {

    navigation(
        startDestination = AddDest.Record.navRoute,
        route = BookTab.Add.navRoute
    ) {

        composable(
            route = AddDest.Record.navRoute,
            deepLinks = listOf(
                navDeepLink { uriPattern = "$APP_DEEPLINK/${AddDest.Record.navRoute}" }
            )
        ) {
            RecordScreen(navigator = navController.toNavigator())
        }

        composable(
            route = "${AddDest.EditCategory.navRoute}/{$ARG_TYPE}",
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

        val settingsRoute = "${AddDest.Settings.navRoute}?$ARG_SHOW_MEMBERS={$ARG_SHOW_MEMBERS}"
        composable(
            route = settingsRoute,
            arguments = listOf(navArgument(ARG_SHOW_MEMBERS) {
                type = NavType.BoolType
                defaultValue = false
            }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "$APP_DEEPLINK/$settingsRoute" }
            )
        ) { entry ->
            val args = entry.arguments ?: Bundle.EMPTY
            SettingsScreen(
                navigator = navController.toNavigator(),
                showMembers = args.getBoolean(ARG_SHOW_MEMBERS, false)
            )
        }

        composable(
            route = AddDest.UnlockPremium.navRoute,
            deepLinks = listOf(
                navDeepLink { uriPattern = "$APP_DEEPLINK/${AddDest.UnlockPremium.navRoute}" }
            )
        ) {
            PremiumScreen(navigator = navController.toNavigator())
        }

        composable(route = AddDest.BatchRecord.navRoute) {
            BatchRecordScreen(navigator = navController.toNavigator())
        }

        val colorToneRoute = "${AddDest.Colors.navRoute}?$COLORS_LINK_QUERY={$COLORS_LINK_QUERY}"
        composable(
            route = colorToneRoute,
            arguments = listOf(navArgument(COLORS_LINK_QUERY) {
                type = NavType.StringType
                nullable = true
            }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "$APP_DEEPLINK/$colorToneRoute" }
            )
        ) { entry ->
            val args = entry.arguments ?: Bundle.EMPTY
            ColorTonePickerScreen(
                navigator = navController.toNavigator(),
                hexFromLink = args.getString(COLORS_LINK_QUERY)
            )
        }

        composable(route = AddDest.CurrencyPicker.navRoute) {
            CurrencyPickerScreen(navigator = navController.toNavigator())
        }

        // Internal screens
        composable(route = AddDest.Insider.navRoute) {
            InsiderScreen(navigator = navController.toNavigator())
        }

        composable(route = AddDest.PushNotifications.navRoute) {
            PushNotificationsScreen(navigator = navController.toNavigator())
        }
    }
}