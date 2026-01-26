package com.kevlina.budgetplus.app

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.kevlina.budgetplus.core.ads.AdMobInitializer
import com.kevlina.budgetplus.core.common.ActivityProviderImpl
import com.kevlina.budgetplus.core.common.AppStartAction
import com.kevlina.budgetplus.core.common.di.HasServiceProvider
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.createGraphFactory
import timber.log.Timber

class BudgetPlusApp : Application(), HasServiceProvider {

    @Inject lateinit var adMobInitializer: AdMobInitializer
    @Inject lateinit var activityProvider: ActivityProviderImpl
    @Inject lateinit var appStartActions: Set<AppStartAction>
    @Inject @Named("is_debug") var isDebug: Boolean = false

    private val appGraph by lazy {
        createGraphFactory<BudgetPlusAppGraph.Factory>().create(this)
    }

    override fun onCreate() {
        appGraph.inject(this)
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

    @Suppress("UNCHECKED_CAST")
    override fun <T> resolve(): T = appGraph as T
}