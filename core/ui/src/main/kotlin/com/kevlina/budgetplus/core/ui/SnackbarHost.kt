package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kevlina.budgetplus.core.common.SnackbarData
import com.kevlina.budgetplus.core.common.SnackbarDuration
import com.kevlina.budgetplus.core.theme.LocalAppColors
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarDuration as MaterialSnackbarDuration
import androidx.compose.material3.SnackbarHost as MaterialSnackbarHost

@Composable
fun SnackbarHost(snackbarData: SnackbarData?) {

    val hostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = snackbarData) {
        if (snackbarData != null) {
            // Dismiss the previous snackbar if it exists, to avoid a long queue
            hostState.currentSnackbarData?.dismiss()
            scope.launch {
                val result = hostState.showSnackbar(
                    message = snackbarData.message,
                    actionLabel = snackbarData.actionLabel,
                    // We support swipe to dismiss, don't want to pollute ui
                    withDismissAction = false,
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

    SwipeableSnackbarHostWrapper(hostState) {
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
}

@Composable
private fun SwipeableSnackbarHostWrapper(
    state: SnackbarHostState,
    modifier: Modifier = Modifier,
    dismissContent: @Composable RowScope.() -> Unit,
) {
    val dismissSnackbarState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.Settled -> false
                SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.EndToStart -> {
                    state.currentSnackbarData?.dismiss()
                    true
                }
            }
        }
    )

    LaunchedEffect(dismissSnackbarState.currentValue) {
        if (dismissSnackbarState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissSnackbarState.reset()
        }
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissSnackbarState,
        backgroundContent = {},
        content = dismissContent,
    )
}

@Preview
@Composable
private fun SnackbarHost_Preview() = AppTheme {
    SnackbarHost(SnackbarData(message = "Update is ready!", actionLabel = "Continue"))
}