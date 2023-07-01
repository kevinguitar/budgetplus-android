package com.kevlina.budgetplus.core.common

import android.content.Context
import com.kevlina.budgetplus.core.common.impl.StringProviderImpl
import com.kevlina.budgetplus.core.common.impl.ToasterImpl
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
        @Named("privacy_policy_url")
        fun providePrivacyPolicyUrl(): String {
            return "https://www.privacypolicygenerator.info/live.php?token=uUzTqhsT9MKWC1QVBjZhUULB1xnmhiJg"
        }
    }
}