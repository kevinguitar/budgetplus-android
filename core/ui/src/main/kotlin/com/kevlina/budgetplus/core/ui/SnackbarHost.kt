package com.kevlina.budgetplus.core.ui

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarDuration as MaterialSnackbarDuration
import androidx.compose.material3.SnackbarHost as MaterialSnackbarHost

@Composable
fun SnackbarHost(snackbarData: SnackbarData?) {

    val hostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = snackbarData) {
        if (snackbarData != null) {
            scope.launch {
                val result = hostState.showSnackbar(
                    message = snackbarData.message,
                    actionLabel = snackbarData.actionLabel,
                    withDismissAction = snackbarData.canDismiss,
                    duration = when (snackbarData.duration) {
                        SnackbarDuration.Short -> MaterialSnackbarDuration.Short
                        SnackbarDuration.Long -> MaterialSnackbarDuration.Long
                        SnackbarDuration.Indefinite -> MaterialSnackbarDuration.Indefinite
                    }
                )
                if (result == SnackbarResult.ActionPerformed) {
                    snackbarData.action()
                }
            }
        }
    }

    MaterialSnackbarHost(
        hostState = hostState,
    ) { data ->

        Snackbar(
            snackbarData = data,
            containerColor = LocalAppColors.current.dark,
            contentColor = LocalAppColors.current.light,
            actionColor = LocalAppColors.current.light,
            actionContentColor = LocalAppColors.current.light,
            dismissActionContentColor = LocalAppColors.current.light,
        )
    }
}

class SnackbarData(
    val message: String,
    val actionLabel: String?,
    val canDismiss: Boolean = false,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val action: () -> Unit,
)

enum class SnackbarDuration {
    Short, Long, Indefinite
}