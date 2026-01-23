package com.kevlina.budgetplus.core.common

import androidx.activity.ComponentActivity

/**
 * Get the current resumed activity, useful for lifecycle ViewModel where you cannot inject Activity.
 */
interface ActivityProvider {
    // Make it nonnull later
    val currentActivity: ComponentActivity?
}
