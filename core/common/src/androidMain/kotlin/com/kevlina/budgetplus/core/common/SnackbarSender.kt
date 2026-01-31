package com.kevlina.budgetplus.core.common

import org.jetbrains.compose.resources.StringResource

/**
 *  Deliver the snackbar event to let ui show it.
 */
interface SnackbarSender {

    val snackbarEvent: EventFlow<SnackbarData>

    fun send(
        message: StringResource,
        actionLabel: StringResource? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: () -> Unit = {},
    )

    fun send(
        message: String,
        actionLabel: StringResource? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: () -> Unit = {},
    )

    fun sendError(e: Exception)
}

class SnackbarData(
    val message: String,
    val actionLabel: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val action: () -> Unit = {},
)

enum class SnackbarDuration {
    Short, Long, Indefinite
}