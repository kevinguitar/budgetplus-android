package com.kevlina.budgetplus.core.common.impl

import android.os.Bundle
import androidx.annotation.RestrictTo
import com.kevlina.budgetplus.core.common.Tracker

@RestrictTo(RestrictTo.Scope.TESTS)
class FakeTracker : Tracker {

    var lastEvent: Pair<String, Bundle?>? = null

    val lastEventName get() = lastEvent?.first

    override fun logEvent(event: String, params: Bundle?) {
        lastEvent = event to params
    }
}