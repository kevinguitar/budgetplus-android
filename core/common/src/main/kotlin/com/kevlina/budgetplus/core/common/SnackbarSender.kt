package com.kevlina.budgetplus.core.common

import androidx.annotation.StringRes

/**
 *  Deliver the snackbar event to let ui show it.
 */
interface SnackbarSender {

    val snackbarEvent: EventFlow<SnackbarData>

    fun send(
        @StringRes message: Int,
        @StringRes actionLabel: Int? = null,
        canDismiss: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: () -> Unit = {},
    )

    fun send(
        message: String,
        @StringRes actionLabel: Int? = null,
        canDismiss: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: () -> Unit = {},
    )

    fun sendError(e: Exception)
}

class SnackbarData(
    val message: String,
    val actionLabel: String? = null,
    val canDismiss: Boolean = false,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val action: () -> Unit = {},
)

enum class SnackbarDuration {
    Short, Long, Indefinite
}