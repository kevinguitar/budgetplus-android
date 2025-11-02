package com.kevlina.budgetplus.insider.app.main

import android.content.Context
import android.content.Intent
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.insider.app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object InsiderModule {

    @Provides @Named("book")
    fun provideBookNavigationAction(@ApplicationContext context: Context): NavigationAction {
        return NavigationAction(intent = Intent(context, InsiderActivity::class.java))
    }

    @Provides @Named("welcome")
    fun provideWelcomeNavigationAction(@ApplicationContext context: Context): NavigationAction {
        return NavigationAction(intent = Intent(context, InsiderActivity::class.java))
    }

    // Do not override FCM token from insider app, to make sure regular users
    // can still receive push notifications from the main B+ app.
    @Provides
    @Named("allow_update_fcm_token")
    fun provideAllowUpdateFcmToken(): Boolean = false

    @Provides
    @Named("google_api_key")
    fun provideGoogleApiKey(): String = BuildConfig.GOOGLE_API_KEY
}