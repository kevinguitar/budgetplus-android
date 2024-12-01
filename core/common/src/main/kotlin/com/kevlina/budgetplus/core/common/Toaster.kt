package com.kevlina.budgetplus.core.common

import androidx.annotation.MainThread

interface Toaster {

    @MainThread
    fun showMessage(message: CharSequence)
}