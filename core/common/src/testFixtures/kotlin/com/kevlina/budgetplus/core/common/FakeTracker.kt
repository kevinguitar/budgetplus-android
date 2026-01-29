package com.kevlina.budgetplus.core.common

import android.os.Bundle

class FakeTracker : Tracker {

    var lastEvent: Pair<String, Bundle?>? = null

    val lastEventName get() = lastEvent?.first

    override fun logEvent(event: String, params: Bundle?) {
        lastEvent = event to params
    }
}