package com.kevlina.budgetplus.core.common

import androidx.compose.runtime.Stable

@Stable
class EventTrigger<T> {

    val event: EventFlow<T>
        field = MutableEventFlow<T>()

    fun sendEvent(content: T) {
        event.sendEvent(content)
    }
}