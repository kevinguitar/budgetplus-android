package com.kevlina.budgetplus.core.common.fixtures

import androidx.annotation.VisibleForTesting
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.SnackbarData
import com.kevlina.budgetplus.core.common.SnackbarDuration
import com.kevlina.budgetplus.core.common.SnackbarSender
import org.jetbrains.compose.resources.StringResource

@VisibleForTesting
object FakeSnackbarSender : SnackbarSender {

    var lastSentMessageRes: StringResource? = null
    var lastSentMessage: String? = null
    var lastSentError: Exception? = null

    override val snackbarEvent = MutableEventFlow<SnackbarData>()

    override fun send(
        message: StringResource,
        actionLabel: StringResource?,
        duration: SnackbarDuration,
        action: () -> Unit,
    ) {
        lastSentMessageRes = message
    }

    override fun send(
        message: String,
        actionLabel: StringResource?,
        duration: SnackbarDuration,
        action: () -> Unit,
    ) {
        lastSentMessage = message
    }

    override fun sendError(e: Exception) {
        lastSentError = e
    }
}