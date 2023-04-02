package com.kevlina.budgetplus.core.common.impl

import android.content.Context
import android.widget.Toast
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.Toaster
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ToasterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : Toaster {

    private var toast: Toast? = null
    private var lastMessage: CharSequence? = null

    override fun showMessage(charSequence: CharSequence) {
        // Cancel duplicated messages to avoid a long queue
        if (lastMessage == charSequence) {
            toast?.cancel()
        }

        toast = Toast.makeText(context, charSequence, Toast.LENGTH_SHORT)
        toast?.show()
        lastMessage = charSequence
    }

    override fun showMessage(resId: Int) {
        showMessage(context.getString(resId))
    }

    override fun showError(e: Exception) {
        Timber.e(e)
        Firebase.crashlytics.recordException(e)
        val error = e.localizedMessage ?: e.message
        if (error != null) {
            showMessage(error)
        } else {
            showMessage(R.string.fallback_error_message)
        }
    }
}