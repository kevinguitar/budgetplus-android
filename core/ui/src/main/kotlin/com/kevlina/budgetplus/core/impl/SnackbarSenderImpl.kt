package com.kevlina.budgetplus.core.impl

import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.ui.SnackbarData
import com.kevlina.budgetplus.core.ui.SnackbarDuration
import com.kevlina.budgetplus.core.ui.SnackbarSender
import kotlinx.coroutines.CancellationException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarSenderImpl @Inject constructor(
    private val stringProvider: StringProvider,
) : SnackbarSender {

    private val _snackbarEvent = MutableEventFlow<SnackbarData>()
    val snackbarEvent: EventFlow<SnackbarData> get() = _snackbarEvent

    override fun send(
        message: Int,
        actionLabel: Int?,
        canDismiss: Boolean,
        duration: SnackbarDuration,
        action: () -> Unit,
    ) = send(
        message = stringProvider[message],
        actionLabel = actionLabel,
        canDismiss = canDismiss,
        duration = duration,
        action = action
    )

    override fun send(
        message: String,
        actionLabel: Int?,
        canDismiss: Boolean,
        duration: SnackbarDuration,
        action: () -> Unit,
    ) {
        _snackbarEvent.sendEvent(SnackbarData(
            message = message,
            actionLabel = actionLabel?.let(stringProvider::get),
            canDismiss = canDismiss,
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
            error != null -> send(error, canDismiss = true)
            else -> send(R.string.fallback_error_message, canDismiss = true)
        }
    }
}