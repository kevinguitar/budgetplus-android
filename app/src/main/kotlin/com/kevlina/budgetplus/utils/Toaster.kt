package com.kevlina.budgetplus.utils

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import com.kevlina.budgetplus.core.common.R
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface Toaster {

    @MainThread
    fun showMessage(charSequence: CharSequence)

    @MainThread
    fun showMessage(@StringRes resId: Int)

    @MainThread
    fun showError(e: Exception)
}

@Singleton
class ToasterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : Toaster {

    private var toast: Toast? = null
    private var lastMessage: CharSequence? = null

    override fun showMessage(charSequence: CharSequence) {
        // Cancel duplicated messages to avoid a long queue
        if (lastMessage == charSequence) {
            toast?.cancel()
        }

        toast = Toast.makeText(context, charSequence, LENGTH_SHORT)
        toast?.show()
        lastMessage = charSequence
    }

    override fun showMessage(resId: Int) {
        showMessage(context.getString(resId))
    }

    override fun showError(e: Exception) {
        Timber.e(e)
        val error = e.localizedMessage ?: e.message
        if (error != null) {
            showMessage(error)
        } else {
            showMessage(R.string.fallback_error_message)
        }
    }
}