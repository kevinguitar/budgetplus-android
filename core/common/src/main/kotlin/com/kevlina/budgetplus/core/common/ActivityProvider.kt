package com.kevlina.budgetplus.core.common

import android.app.Application
import androidx.activity.ComponentActivity

/**
 * Get the current resumed activity, useful for lifecycle ViewModel where you cannot inject Activity.
 */
interface ActivityProvider : Application.ActivityLifecycleCallbacks {
    //TODO: Make it nonnull later
    val currentActivity: ComponentActivity?
}
