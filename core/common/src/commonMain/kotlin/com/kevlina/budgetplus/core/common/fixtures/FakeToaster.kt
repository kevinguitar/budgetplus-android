package com.kevlina.budgetplus.core.common.fixtures

import androidx.annotation.VisibleForTesting
import com.kevlina.budgetplus.core.common.Toaster

@VisibleForTesting
object FakeToaster : Toaster {

    var lastShownMessage: CharSequence? = null

    override fun showMessage(message: CharSequence) {
        lastShownMessage = message
    }
}