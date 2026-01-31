package com.kevlina.budgetplus.core.common

import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.fallback_error_message
import co.touchlab.kermit.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import org.jetbrains.compose.resources.StringResource

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SnackbarSenderImpl(
    private val stringProvider: StringProvider,
) : SnackbarSender {

    final override val snackbarEvent: EventFlow<SnackbarData>
        field = MutableEventFlow<SnackbarData>()

    override fun send(
        message: StringResource,
        actionLabel: StringResource?,
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
        actionLabel: StringResource?,
        duration: SnackbarDuration,
        action: () -> Unit,
    ) {
        Logger.d { "SnackbarSender: Show snackbar $message" }
        snackbarEvent.sendEvent(SnackbarData(
            message = message,
            actionLabel = actionLabel?.let(stringProvider::get),
            duration = duration,
            action = action
        ))
    }

    override fun sendError(e: Exception) {
        Logger.e(e) { "SnackbarSender: sendError" }
        val error = e.localizedMessage ?: e.message
        when {
            // Do not toast the cancellation error
            e is CancellationException -> Unit
            error != null -> send(error)
            else -> send(Res.string.fallback_error_message)
        }
    }
}