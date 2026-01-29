package com.kevlina.budgetplus.core.common.fixtures

import androidx.annotation.VisibleForTesting
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.SnackbarData
import com.kevlina.budgetplus.core.common.SnackbarDuration
import com.kevlina.budgetplus.core.common.SnackbarSender

@VisibleForTesting
object FakeSnackbarSender : SnackbarSender {

    var lastSentMessageId: Int? = null
    var lastSentMessage: String? = null
    var lastSentError: Exception? = null

    override val snackbarEvent = MutableEventFlow<SnackbarData>()

    override fun send(
        message: Int,
        actionLabel: Int?,
        duration: SnackbarDuration,
        action: () -> Unit,
    ) {
        lastSentMessageId = message
    }

    override fun send(
        message: String,
        actionLabel: Int?,
        duration: SnackbarDuration,
        action: () -> Unit,
    ) {
        lastSentMessage = message
    }

    override fun sendError(e: Exception) {
        lastSentError = e
    }
}