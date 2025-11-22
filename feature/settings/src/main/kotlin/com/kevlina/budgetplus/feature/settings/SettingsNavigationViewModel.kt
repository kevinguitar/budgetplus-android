package com.kevlina.budgetplus.feature.settings

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.kevlina.budgetplus.core.common.ActivityProvider
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AuthManager
import dev.zacsweers.metro.Inject
import javax.inject.Named

@Inject
internal class SettingsNavigationViewModel(
    private val authManager: AuthManager,
    private val activityProvider: ActivityProvider,
    private val navigationFlow: NavigationFlow,
    private val stringProvider: StringProvider,
    private val snackbarSender: SnackbarSender,
    private val tracker: Tracker,
    @Named("app_package") private val appPackage: String,
    @Named("google_play_url") private val googlePlayUrl: String,
    @Named("instagram_url") private val instagramUrl: String,
    @Named("privacy_policy_url") private val privacyPolicyUrl: String,
    @Named("contact_email") private val contactEmail: String,
    @Named("logout") private val logoutNavigationAction: NavigationAction,
) {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun openLanguageSettings() {
        val activity = activityProvider.currentActivity ?: return
        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
            data = "package:$appPackage".toUri()
        }
        activity.startActivity(intent)
        tracker.logEvent("settings_language_click")
    }

    fun share() {
        val activity = activityProvider.currentActivity ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, stringProvider[R.string.settings_share_app_message, googlePlayUrl])
        }
        activity.startActivity(Intent.createChooser(intent, stringProvider[R.string.settings_share_app]))
        tracker.logEvent("settings_share_app_click")
    }

    fun rateUs() {
        visitUrl(googlePlayUrl)
        tracker.logEvent("settings_rate_us_click")
    }

    fun followOnInstagram() {
        visitUrl(instagramUrl)
        tracker.logEvent("settings_follow_instagram_click")
    }

    fun contactUs() {
        val activity = activityProvider.currentActivity ?: return
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(contactEmail))
            putExtra(Intent.EXTRA_SUBJECT, stringProvider[R.string.settings_contact_us])
            putExtra(Intent.EXTRA_TEXT, "User id: ${authManager.requireUserId()}\n\n")
        }
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
            tracker.logEvent("settings_contact_us_click")
        } else {
            snackbarSender.send(R.string.settings_no_email_app_found)
        }
    }

    fun viewPrivacyPolicy() {
        visitUrl(privacyPolicyUrl)
        tracker.logEvent("settings_privacy_policy_click")
    }

    fun logout() {
        authManager.logout()
        navigationFlow.sendEvent(logoutNavigationAction)
    }

    private fun visitUrl(url: String) {
        val activity = activityProvider.currentActivity ?: return
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = url.toUri()
        }
        activity.startActivity(intent)
    }
}