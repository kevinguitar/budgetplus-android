package com.kevlina.budgetplus.core.common

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.asStateFlow

@Stable
class EventTrigger<T> {

    private val _event = MutableEventFlow<T>()
    val event: EventFlow<T> = _event.asStateFlow()

    fun sendEvent(content: T) {
        _event.sendEvent(content)
    }
}