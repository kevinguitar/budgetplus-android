package com.kevlina.budgetplus.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import timber.log.Timber

// See https://issuetracker.google.com/issues/358306222
@Composable
fun ClearFocusOnPauseEffect() {
    val focusManager = rememberSafeFocusManager()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                Timber.d("Clearing focus on pause to workaround a crash")
                // "force = true" is critical here to immediately sever
                // the link to the potentially crashing node.
                focusManager.clearFocus(force = true)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}