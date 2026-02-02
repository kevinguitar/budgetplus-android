package com.kevlina.budgetplus.app

import android.app.Application
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.Logger
import com.google.firebase.analytics.analytics
import com.kevlina.budgetplus.core.ads.AdMobInitializer
import com.kevlina.budgetplus.core.common.ActivityProviderImpl
import com.kevlina.budgetplus.core.common.AppCoroutineScope
import com.kevlina.budgetplus.core.common.AppStartAction
import com.kevlina.budgetplus.core.common.di.HasServiceProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BudgetPlusApp : Application(), HasServiceProvider {

    @Inject lateinit var adMobInitializer: AdMobInitializer
    @Inject lateinit var activityProvider: ActivityProviderImpl
    @Inject lateinit var appStartActions: Set<AppStartAction>
    @Inject @AppCoroutineScope lateinit var appScope: CoroutineScope
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

        Logger.setLogWriters(if (isDebug) LogcatWriter() else CrashReportingLogWriter())
        Firebase.analytics.setAnalyticsCollectionEnabled(!isDebug)
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!isDebug)

        // Execute all the non-blocking actions that need to be executed on app start.
        appStartActions.forEach {
            appScope.launch { it.onAppStart()  }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> resolve(): T = appGraph as T
}