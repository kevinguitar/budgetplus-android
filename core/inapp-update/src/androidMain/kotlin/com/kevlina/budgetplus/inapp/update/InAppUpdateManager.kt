package com.kevlina.budgetplus.inapp.update

import kotlinx.coroutines.flow.StateFlow

interface InAppUpdateManager {

    val updateState: StateFlow<InAppUpdateState>

}

sealed class InAppUpdateState {

    data object NotStarted : InAppUpdateState()

    data object NotAvailable : InAppUpdateState()

    data object Downloading : InAppUpdateState()

    data class Downloaded(val complete: () -> Unit) : InAppUpdateState()

}