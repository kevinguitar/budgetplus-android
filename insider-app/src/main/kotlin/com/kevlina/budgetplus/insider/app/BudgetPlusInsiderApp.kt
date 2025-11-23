package com.kevlina.budgetplus.insider.app

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.kevlina.budgetplus.core.common.ActivityProviderImpl
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BudgetPlusInsiderApp : Application() {

    @Inject lateinit var activityProvider: ActivityProviderImpl

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(activityProvider)

        Timber.plant(Timber.DebugTree())
        Firebase.analytics.setAnalyticsCollectionEnabled(false)
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = false
    }
}