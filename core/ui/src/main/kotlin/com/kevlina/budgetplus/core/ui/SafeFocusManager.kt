package com.kevlina.budgetplus.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import timber.log.Timber

@Composable
fun rememberSafeFocusManager(): FocusManager {
    val focusManager = LocalFocusManager.current
    return remember(focusManager) { SafeFocusManager(focusManager) }
}

// A workaround of clear focus crash
// https://issuetracker.google.com/issues/358306222
private class SafeFocusManager(
    private val focusManager: FocusManager,
) : FocusManager by focusManager {
    override fun clearFocus(force: Boolean) {
        try {
            focusManager.clearFocus(force)
        } catch (e: Exception) {
            Timber.e(e, "Fail to clear focus")
        }
    }
}