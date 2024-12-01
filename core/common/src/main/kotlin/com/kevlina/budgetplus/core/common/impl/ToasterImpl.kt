package com.kevlina.budgetplus.core.common.impl

import android.content.Context
import android.widget.Toast
import com.kevlina.budgetplus.core.common.Toaster
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ToasterImpl @Inject constructor(
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
        lastMessage = message
    }
}