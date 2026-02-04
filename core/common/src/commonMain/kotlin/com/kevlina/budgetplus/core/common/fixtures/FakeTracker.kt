package com.kevlina.budgetplus.core.common.fixtures

import androidx.annotation.VisibleForTesting
import com.kevlina.budgetplus.core.common.Tracker

@VisibleForTesting
class FakeTracker : Tracker {

    var lastEvent: Pair<String, Map<String, Any>?>? = null

    val lastEventName get() = lastEvent?.first

    override fun logEvent(event: String, params: Map<String, Any>?) {
        lastEvent = event to params
    }
}