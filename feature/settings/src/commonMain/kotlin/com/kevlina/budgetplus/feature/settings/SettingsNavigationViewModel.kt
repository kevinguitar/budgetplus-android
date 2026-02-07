package com.kevlina.budgetplus.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.settings_share_app
import budgetplus.core.common.generated.resources.settings_share_app_message
import com.kevlina.budgetplus.core.common.ShareHelper
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AuthManager
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

@Inject
internal class SettingsNavigationViewModel(
    private val authManager: AuthManager,
    private val navigation: SettingsNavigation,
    private val shareHelper: ShareHelper,
    private val navigationFlow: NavigationFlow,
    private val tracker: Tracker,
    @Named("google_play_url") private val googlePlayUrl: String,
    @Named("instagram_url") private val instagramUrl: String,
    @Named("privacy_policy_url") private val privacyPolicyUrl: String,
    @Named("auth") private val authNavigationAction: NavigationAction,
) : ViewModel() {

    fun openLanguageSettings() {
        navigation.openLanguageSettings()
        tracker.logEvent("settings_language_click")
    }

    fun share() {
        viewModelScope.launch {
            shareHelper.share(
                title = Res.string.settings_share_app,
                text = getString(Res.string.settings_share_app_message, googlePlayUrl)
            )
            tracker.logEvent("settings_share_app_click")
        }
    }

    fun rateUs() {
        navigation.visitUrl(googlePlayUrl)
        tracker.logEvent("settings_rate_us_click")
    }

    fun followOnInstagram() {
        navigation.visitUrl(instagramUrl)
        tracker.logEvent("settings_follow_instagram_click")
    }

    fun contactUs() {
        viewModelScope.launch {
            navigation.contactUs()
            tracker.logEvent("settings_contact_us_click")
        }
    }

    fun viewPrivacyPolicy() {
        navigation.visitUrl(privacyPolicyUrl)
        tracker.logEvent("settings_privacy_policy_click")
    }

    fun logout() {
        authManager.logout()
        navigationFlow.sendEvent(authNavigationAction)
    }
}