package com.kevlina.budgetplus.core.common

import androidx.annotation.MainThread
import androidx.annotation.StringRes

interface Toaster {

    @MainThread
    fun showMessage(charSequence: CharSequence)

    @MainThread
    fun showMessage(@StringRes resId: Int)

    @MainThread
    fun showError(e: Exception)
}