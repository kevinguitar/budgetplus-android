package com.kevlina.budgetplus.core.common.impl

import androidx.annotation.RestrictTo
import com.kevlina.budgetplus.core.common.Toaster

@RestrictTo(RestrictTo.Scope.TESTS)
class FakeToaster(
    private val stringProvider: FakeStringProvider = FakeStringProvider(),
) : Toaster {

    var lastShownMessage: CharSequence? = null
    var lastError: Exception? = null

    override fun showMessage(message: CharSequence) {
        lastShownMessage = message
    }

    override fun showMessage(resId: Int) {
        lastShownMessage = stringProvider[resId]
    }

    override fun showError(e: Exception) {
        lastError = e
    }
}