package com.kevlina.budgetplus.core.common

interface Tracker {

    fun logEvent(event: String, params: Map<String, Any>? = null)

}