package com.kevlina.budgetplus.core.common

import kotlinx.coroutines.CancellationException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SnackbarSenderImpl @Inject constructor(
    private val stringProvider: StringProvider,
) : SnackbarSender {

    private val _snackbarEvent = MutableEventFlow<SnackbarData>()
    override val snackbarEvent: EventFlow<SnackbarData> get() = _snackbarEvent

    override fun send(
        message: Int,
        actionLabel: Int?,
        duration: SnackbarDuration,
        action: () -> Unit,
    ) = send(
        message = stringProvider[message],
        actionLabel = actionLabel,
        duration = duration,
        action = action
    )

    override fun send(
        message: String,
        actionLabel: Int?,
        duration: SnackbarDuration,
        action: () -> Unit,
    ) {
        Timber.d("SnackbarSender: Show snackbar $message")
        _snackbarEvent.sendEvent(SnackbarData(
            message = message,
            actionLabel = actionLabel?.let(stringProvider::get),
            duration = duration,
            action = action
        ))
    }

    override fun sendError(e: Exception) {
        Timber.e(e)
        val error = e.localizedMessage ?: e.message
        when {
            // Do not toast the cancellation error
            e is CancellationException -> Unit
            error != null -> send(error)
            else -> send(R.string.fallback_error_message)
        }
    }
}