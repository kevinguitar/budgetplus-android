package com.kevlina.budgetplus

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.core.data.UserRepo
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BudgetPlusApp : Application() {

    // Dependencies that need to be instantiated upon app launch
    @Inject lateinit var userRepo: UserRepo

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this)

        Timber.plant(Timber.DebugTree())
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}