package com.kevlina.budgetplus.core.common

import android.app.Activity
import android.os.Bundle
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ActivityProviderImpl @Inject constructor() : ActivityProvider {

    private var _currentActivity: Activity? = null

    override val currentActivity: Activity?
        get() = _currentActivity ?: run {
            Timber.e(MissingActivityException())
            null
        }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        _currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        _currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        _currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (_currentActivity == activity) {
            _currentActivity = null
        }
    }

    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}

private class MissingActivityException : RuntimeException("Current activity is null")
