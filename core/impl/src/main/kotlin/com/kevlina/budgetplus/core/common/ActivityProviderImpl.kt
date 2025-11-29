package com.kevlina.budgetplus.core.common

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import timber.log.Timber

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, binding = binding<ActivityProvider>())
class ActivityProviderImpl : ActivityProvider, Application.ActivityLifecycleCallbacks {

    private var _currentActivity: Activity? = null

    override val currentActivity: ComponentActivity?
        get() = (_currentActivity as? ComponentActivity) ?: run {
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
