package com.kevlina.budgetplus.core.common

import android.content.Context
import android.widget.Toast
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import timber.log.Timber

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class ToasterImpl(
    private val context: Context,
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