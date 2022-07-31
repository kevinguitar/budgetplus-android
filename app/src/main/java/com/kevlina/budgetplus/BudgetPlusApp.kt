package com.kevlina.budgetplus

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BudgetPlusApp : Application() {

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this)

        Timber.plant(Timber.DebugTree())
        //TODO: Disable crash report for debug builds
        //Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}