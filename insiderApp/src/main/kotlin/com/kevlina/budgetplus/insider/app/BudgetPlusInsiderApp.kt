package com.kevlina.budgetplus.insider.app

import android.app.Application
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.Logger
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.kevlina.budgetplus.core.common.ActivityProviderImpl
import com.kevlina.budgetplus.core.common.di.HasServiceProvider
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.createGraphFactory

class BudgetPlusInsiderApp : Application(), HasServiceProvider {

    @Inject lateinit var activityProvider: ActivityProviderImpl

    private val appGraph by lazy {
        createGraphFactory<BudgetPlusInsiderAppGraph.Factory>().create(this)
    }

    override fun onCreate() {
        appGraph.inject(this)
        super.onCreate()

        registerActivityLifecycleCallbacks(activityProvider)

        Logger.setLogWriters(LogcatWriter())
        Firebase.analytics.setAnalyticsCollectionEnabled(false)
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = false
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> resolve(): T = appGraph as T
}