package com.kevlina.budgetplus.core.ui

import androidx.annotation.StringRes

/**
 *  Deliver the snackbar event to let BookActivity show it.
 */
interface SnackbarSender {

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
