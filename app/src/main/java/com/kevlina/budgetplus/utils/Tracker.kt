package com.kevlina.budgetplus.utils

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.BuildConfig
import com.kevlina.budgetplus.auth.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Tracker @Inject constructor(
    authManager: AuthManager,
    @AppScope appScope: CoroutineScope
) {

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

    fun logEvent(event: String) {
        analytics.logEvent(event, null)
    }

    fun logEvent(event: String, params: Map<String, String>) {
        analytics.logEvent(event) {
            params.forEach(::param)
        }
    }
}