package com.kevlina.budgetplus.app

import android.app.Application
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.core.ads.AdMobInitializer
import com.kevlina.budgetplus.core.common.ActivityProviderImpl
import com.kevlina.budgetplus.core.common.AppStartAction
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class BudgetPlusApp : Application() {

    @Inject lateinit var adMobInitializer: AdMobInitializer
    @Inject lateinit var activityProvider: ActivityProviderImpl
    @Inject lateinit var appStartActions: Set<@JvmSuppressWildcards AppStartAction>
    @Inject @JvmField @Named("is_debug") var isDebug: Boolean = false

    override fun onCreate() {
        super.onCreate()

        // Initialize AdMob asap after app starts
        adMobInitializer.initialize()

        registerActivityLifecycleCallbacks(activityProvider)

        Timber.plant(if (isDebug) Timber.DebugTree() else CrashReportingTree())
        Firebase.analytics.setAnalyticsCollectionEnabled(!isDebug)
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = !isDebug

        // Execute all the actions that need to be executed on app start.
        appStartActions.forEach { it.onAppStart() }
    }
}