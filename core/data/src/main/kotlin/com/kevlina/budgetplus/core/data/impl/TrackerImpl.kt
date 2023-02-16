package com.kevlina.budgetplus.core.data.impl

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.BuildConfig
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TrackerImpl @Inject constructor(
    authManager: AuthManager,
    @AppScope appScope: CoroutineScope,
) : Tracker {

    private val analytics by lazy {
        Firebase.analytics.apply {
            setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
        }
    }

    init {
        authManager.userState
            .onEach { analytics.setUserId(it?.id) }
            .launchIn(appScope)
    }

    override fun logEvent(event: String) {
        if (!BuildConfig.DEBUG) {
            analytics.logEvent(event, null)
        }
    }
}