package com.kevlina.budgetplus.insider.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.kevlina.budgetplus.core.common.ActivityProviderImpl
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class BudgetPlusInsiderApp : Application() {

    @Inject lateinit var activityProvider: ActivityProviderImpl
    @Inject @JvmField @Named("is_debug") var isDebug: Boolean = false

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        registerActivityLifecycleCallbacks(activityProvider)

        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        }
    }
}