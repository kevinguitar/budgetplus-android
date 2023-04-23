package com.kevlina.budgetplus

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.core.common.AppStartAction
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class BudgetPlusApp : Application() {

    @Inject lateinit var appStartActions: Set<@JvmSuppressWildcards AppStartAction>
    @Inject @JvmField @Named("is_debug") var isDebug: Boolean = false

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this)

        Timber.plant(Timber.DebugTree())
        Firebase.analytics.setAnalyticsCollectionEnabled(!isDebug)
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!isDebug)

        // Execute all the actions that need to be executed on app start.
        appStartActions.forEach { it.onAppStart() }
    }
}