package com.kevlina.budgetplus.core.common.impl

import android.content.Context
import android.widget.Toast
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Toaster
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ToasterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val stringProvider: StringProvider,
) : Toaster {

    private var toast: Toast? = null
    private var lastMessage: CharSequence? = null

    override fun showMessage(message: CharSequence) {
        // Cancel duplicated messages to avoid a long queue
        if (lastMessage == message) {
            toast?.cancel()
        }

        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast?.show()
        lastMessage = message
    }

    override fun showMessage(resId: Int) {
        showMessage(stringProvider[resId])
    }

    override fun showError(e: Exception) {
        Timber.e(e)
        val error = e.localizedMessage ?: e.message
        when {
            // Do not toast the cancellation error
            e is CancellationException -> Unit
            error != null -> showMessage(error)
            else -> showMessage(R.string.fallback_error_message)
        }
    }
}