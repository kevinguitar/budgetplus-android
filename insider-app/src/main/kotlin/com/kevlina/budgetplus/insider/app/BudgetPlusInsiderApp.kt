package com.kevlina.budgetplus.insider.app

import android.app.Application
import com.kevlina.budgetplus.core.common.ActivityProviderImpl
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import timber.log.Timber

class BudgetPlusInsiderApp : Application() {

    @Inject lateinit var activityProvider: ActivityProviderImpl
    @Inject @JvmField @Named("is_debug") var isDebug: Boolean = false

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(activityProvider)

        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        }
    }
}