package com.kevlina.budgetplus.core.di

import android.content.Context
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.is_debug
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.NAV_RECORD_PATH
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

@ContributesTo(AppScope::class)
interface CommonModule {

    // Make it a singleton here for easiness, there is no activity stack at all.
    @Provides
    @SingleIn(AppScope::class)
    fun provideNavigationFlow(): NavigationFlow = MutableEventFlow()

    @Provides
    @Named("is_debug")
    fun provideIsDebug(context: Context): Boolean {
        //TODO: what should I do?
        return runBlocking { getString(Res.string.is_debug).toBooleanStrict() }
    }

    @Provides
    @Named("app_package")
    fun provideAppPackage(): String = "com.kevlina.budgetplus"

    @Provides
    @Named("google_play_url")
    fun provideGooglePlayUrl(
        @Named("app_package") appPackage: String,
    ): String {
        return "https://play.google.com/store/apps/details?id=$appPackage"
    }

    @Provides
    @Named("instagram_url")
    fun provideInstagramUrl(): String = "https://www.instagram.com/budget.plus.tw/"

    @Provides
    @Named("contact_email")
    fun provideContactEmail(): String = "budgetplussg@gmail.com"

    @Provides
    @Named("privacy_policy_url")
    fun providePrivacyPolicyUrl(): String = "https://budgetplus.cchi.tw/privacy-policy/"

    @Provides
    @Named("default_deeplink")
    fun provideDefaultDeeplink(): String = "$APP_DEEPLINK/$NAV_RECORD_PATH"
}