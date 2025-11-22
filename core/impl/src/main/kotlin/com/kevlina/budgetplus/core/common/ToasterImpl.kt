package com.kevlina.budgetplus.core.common

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToasterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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
        Timber.d("Toaster: Show toast $message")
        lastMessage = message
    }
}