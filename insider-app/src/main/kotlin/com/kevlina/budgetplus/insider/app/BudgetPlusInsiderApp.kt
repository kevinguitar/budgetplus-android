package com.kevlina.budgetplus.insider.app

import android.app.Application
import com.kevlina.budgetplus.core.common.ActivityProviderImpl
import com.kevlina.budgetplus.core.common.di.HasServiceProvider
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.createGraphFactory
import timber.log.Timber

class BudgetPlusInsiderApp : Application(), HasServiceProvider {

    @Inject lateinit var activityProvider: ActivityProviderImpl
    @Inject @Named("is_debug") var isDebug: Boolean = false

    private val appGraph by lazy {
        createGraphFactory<BudgetPlusInsiderAppGraph.Factory>().create(this)
    }

    override fun onCreate() {
        appGraph.inject(this)
        super.onCreate()

        registerActivityLifecycleCallbacks(activityProvider)

        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> resolve(): T = appGraph as T
}