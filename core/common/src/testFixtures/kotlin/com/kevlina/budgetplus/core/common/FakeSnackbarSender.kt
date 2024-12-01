package com.kevlina.budgetplus.core.common

object FakeSnackbarSender : SnackbarSender {

    var lastSentMessageId: Int? = null
    var lastSentMessage: String? = null
    var lastSentError: Exception? = null

    override val snackbarEvent = MutableEventFlow<SnackbarData>()

    override fun send(
        message: Int,
        actionLabel: Int?,
        canDismiss: Boolean,
        duration: SnackbarDuration,
        action: () -> Unit,
    ) {
        lastSentMessageId = message
    }

    override fun send(
        message: String,
        actionLabel: Int?,
        canDismiss: Boolean,
        duration: SnackbarDuration,
        action: () -> Unit,
    ) {
        lastSentMessage = message
    }

    override fun sendError(e: Exception) {
        lastSentError = e
    }
}