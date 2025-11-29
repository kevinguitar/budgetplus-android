package com.kevlina.budgetplus.core.di

import android.content.Context
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.NAV_RECORD_PATH
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface CommonModule {

    // Make it a singleton here for easiness, there is no activity stack at all.
    @Provides
    @SingleIn(AppScope::class)
    fun provideNavigationFlow(): NavigationFlow = MutableEventFlow()

    @Provides
    @Named("is_debug")
    fun provideIsDebug(context: Context): Boolean {
        return context.resources.getBoolean(R.bool.is_debug)
    }

    @Provides
    @Named("app_package")
    fun provideAppPackage(): String {
        return "com.kevlina.budgetplus"
    }

    @Provides
    @Named("google_play_url")
    fun provideGooglePlayUrl(
        @Named("app_package") appPackage: String,
    ): String {
        return "https://play.google.com/store/apps/details?id=$appPackage"
    }

    @Provides
    @Named("instagram_url")
    fun provideInstagramUrl(): String {
        return "https://www.instagram.com/budget.plus.tw/"
    }

    @Provides
    @Named("contact_email")
    fun provideContactEmail(): String {
        return "budgetplussg@gmail.com"
    }

    @Provides
    @Named("privacy_policy_url")
    fun providePrivacyPolicyUrl(): String {
        return "https://budgetplus.cchi.tw/privacy-policy/"
    }

    @Provides
    @Named("default_deeplink")
    fun provideDefaultDeeplink(): String {
        return "$APP_DEEPLINK/$NAV_RECORD_PATH"
    }
}