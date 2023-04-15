package com.kevlina.budgetplus.inapp.update

import kotlinx.coroutines.flow.StateFlow

interface InAppUpdateManager {

    val updateState: StateFlow<InAppUpdateState>

}

sealed class InAppUpdateState {

    object NotStarted : InAppUpdateState()

    object NotAvailable : InAppUpdateState()

    object Downloading : InAppUpdateState()

    data class Downloaded(val complete: () -> Unit) : InAppUpdateState()

}