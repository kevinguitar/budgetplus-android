package com.kevlina.budgetplus.book

import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.ui.SnackbarData
import com.kevlina.budgetplus.core.ui.SnackbarSender
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookSnackbarSender @Inject constructor() : SnackbarSender {

    private val _snackbarEvent = MutableEventFlow<SnackbarData>()
    val snackbarEvent: EventFlow<SnackbarData> get() = _snackbarEvent

    override fun showSnackbar(snackbarData: SnackbarData) {
        _snackbarEvent.sendEvent(snackbarData)
    }
}