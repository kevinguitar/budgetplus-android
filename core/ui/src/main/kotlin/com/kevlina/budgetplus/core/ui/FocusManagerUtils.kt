package com.kevlina.budgetplus.core.ui

import androidx.compose.ui.focus.FocusManager
import timber.log.Timber

fun FocusManager.clearFocusSafe() {
    try {
        clearFocus()
    } catch (e: Exception) {
        // https://issuetracker.google.com/issues/358306222
        Timber.e(e, "Fail to clear focus")
    }
}