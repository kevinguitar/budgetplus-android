package com.kevlina.budgetplus.core.common

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// On iOS there is no Toast concept, just propagate events to Snackbar
@ContributesBinding(AppScope::class)
class ToasterImpl(
    private val snackbarSender: SnackbarSender,
    @AppCoroutineScope private val appScope: CoroutineScope,
) : Toaster {
    override fun showMessage(message: CharSequence) {
        appScope.launch { snackbarSender.send(message.toString()) }
    }
}