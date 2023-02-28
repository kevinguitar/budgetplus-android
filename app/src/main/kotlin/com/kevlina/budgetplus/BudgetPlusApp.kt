package com.kevlina.budgetplus

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.core.data.UserRepo
import com.kevlina.budgetplus.notification.channel.NotificationChannelsInitializer
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class BudgetPlusApp : Application() {

    // Dependencies that need to be instantiated upon app launch
    @Inject lateinit var userRepo: UserRepo
    @Inject lateinit var notificationChannelsInitializer: NotificationChannelsInitializer
    @Inject @JvmField @Named("is_debug") var isDebug: Boolean = false

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this)

        Timber.plant(Timber.DebugTree())
        Firebase.analytics.setAnalyticsCollectionEnabled(!isDebug)
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!isDebug)
    }
}