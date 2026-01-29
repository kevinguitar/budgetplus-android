package com.kevlina.budgetplus.core.common.fixtures

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.kevlina.budgetplus.core.common.Tracker

@VisibleForTesting
class FakeTracker : Tracker {

    var lastEvent: Pair<String, Bundle?>? = null

    val lastEventName get() = lastEvent?.first

    override fun logEvent(event: String, params: Bundle?) {
        lastEvent = event to params
    }
}