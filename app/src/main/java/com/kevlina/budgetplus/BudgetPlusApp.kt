package com.kevlina.budgetplus

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BudgetPlusApp : Application() {

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this)

        Timber.plant(Timber.DebugTree())
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}