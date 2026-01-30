package com.kevlina.budgetplus.core.common

import android.widget.Toast
import co.touchlab.kermit.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class ToasterImpl(
    private val context: android.content.Context,
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
        Logger.d { "Toaster: Show toast $message" }
        lastMessage = message
    }
}