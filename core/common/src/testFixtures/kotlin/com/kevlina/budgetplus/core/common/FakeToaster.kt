package com.kevlina.budgetplus.core.common

object FakeToaster : Toaster {

    var lastShownMessage: CharSequence? = null

    override fun showMessage(message: CharSequence) {
        lastShownMessage = message
    }
}