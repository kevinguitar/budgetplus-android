package com.kevlina.budgetplus.core.common.di

import android.content.Context
import com.kevlina.budgetplus.core.common.AppCoroutineScope
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.impl.StringProviderImpl
import com.kevlina.budgetplus.core.common.impl.ToasterImpl
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.NAV_RECORD_PATH
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface CommonModule {

    @Binds
    fun provideStringProvider(impl: StringProviderImpl): StringProvider

    @Binds
    fun provideToaster(impl: ToasterImpl): Toaster

    companion object {

        @Provides
        @AppScope
        @Singleton
        fun provideAppCoroutineScope(): CoroutineScope = AppCoroutineScope

        // Make it a singleton here for easiness, there is no activity stack at all.
        @Provides
        @Singleton
        fun provideNavigationFlow(): NavigationFlow = MutableEventFlow()

        @Provides
        @Named("is_debug")
        fun provideIsDebug(@ApplicationContext context: Context): Boolean {
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
}