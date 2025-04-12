package com.kevlina.budgetplus.core.common

import android.app.Activity
import android.app.Application

/**
 * Get the current resumed activity, useful for lifecycle ViewModel where you cannot inject Activity.
 */
interface ActivityProvider : Application.ActivityLifecycleCallbacks {
    val currentActivity: Activity?
}
