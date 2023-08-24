package com.kevlina.budgetplus.core.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Maps value of one StateFlow to another StateFlow
 *
 * [transform] is not a suspend function because it required for initial value.
 *
 * Careful with side effects when use this function, [transform] will be called on every access
 * to original value, so every time when you call [StateFlow.value] it will call [transform].
 */
fun <T, R> StateFlow<T>.mapState(transform: (T) -> R): StateFlow<R> {
    return MappedStateFlow(this, transform)
}

private class MappedStateFlow<T, R>(
    private val source: StateFlow<T>,
    private val transform: (T) -> R,
) : StateFlow<R> {

    override val value: R
        get() = transform(source.value)

    override val replayCache: List<R>
        get() = source.replayCache.map(transform)

    override suspend fun collect(collector: FlowCollector<R>): Nothing {
        source.collect { value ->
            collector.emit(transform(value))
        }
    }
}

fun <T, S, R> StateFlow<T>.combineState(
    other: StateFlow<S>,
    scope: CoroutineScope,
    sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(),
    transform: (T, S) -> R,
): StateFlow<R> {
    return combine(other) { a, b -> transform(a, b) }
        .stateIn(scope, sharingStarted, transform(value, other.value))
}