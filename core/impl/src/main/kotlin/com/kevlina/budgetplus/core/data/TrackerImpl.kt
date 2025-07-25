package com.kevlina.budgetplus.core.data

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.Tracker
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class TrackerImpl @Inject constructor(
    authManager: Lazy<AuthManager>,
    @AppScope appScope: CoroutineScope,
    @Named("is_debug") private val isDebug: Boolean,
) : Tracker {

    private val analytics by lazy { Firebase.analytics }

    init {
        authManager.get()
            .userState
            .onEach { analytics.setUserId(it?.id) }
            .launchIn(appScope)
    }

    override fun logEvent(event: String, params: Bundle?) {
        Timber.d("Analytics:: $event" + if (params == null) "" else ", $params")
        if (!isDebug) {
            analytics.logEvent(event, params)
        }
    }
}