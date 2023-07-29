package com.kevlina.budgetplus.core.common

import android.os.Bundle

interface Tracker {

    fun logEvent(event: String, params: Bundle? = null)

}