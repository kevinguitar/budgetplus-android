package com.kevlina.budgetplus.core.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach

data class Event<out T>(private val content: T) {
    var consumed = false
        private set

    fun consume(): T? {
        return if (consumed) {
            null
        } else {
            consumed = true
            content
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event<*>

        if (content != other.content) return false
        if (consumed != other.consumed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content?.hashCode() ?: 0
        result = 31 * result + consumed.hashCode()
        return result
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        val CONSUMED: Event<Nothing> = Event(null).apply { consumed = true } as Event<Nothing>
    }
}

typealias EventFlow<T> = StateFlow<Event<T>>
typealias MutableEventFlow<T> = MutableStateFlow<Event<T>>

@Suppress("FunctionName")
fun <T> MutableEventFlow() = MutableStateFlow<Event<T>>(Event.CONSUMED)

fun <T> MutableStateFlow<Event<T>>.sendEvent(content: T) {
    value = Event(content)
}

fun MutableStateFlow<Event<Unit>>.sendEvent() = sendEvent(Unit)

fun <T> StateFlow<Event<T>?>.consumeEach(
    block: suspend (T) -> Unit,
) = onEach {
    val value = it?.consume()
    if (value != null) {
        block(value)
    }
}