package com.kevlina.budgetplus.core.common.impl

import com.kevlina.budgetplus.core.common.Toaster
import org.jetbrains.annotations.VisibleForTesting

@VisibleForTesting
class FakeToaster : Toaster {

    override fun showMessage(charSequence: CharSequence) {
        error("Not yet implemented")
    }

    override fun showMessage(resId: Int) {
        error("Not yet implemented")
    }

    override fun showError(e: Exception) {
        error("Not yet implemented")
    }
}