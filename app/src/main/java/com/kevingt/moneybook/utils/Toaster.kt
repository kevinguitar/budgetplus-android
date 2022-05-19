package com.kevingt.moneybook.utils

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import com.kevingt.moneybook.R
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
    @ApplicationContext private val context: Context
) : Toaster {

    private val toast by lazy {
        Toast.makeText(context, null, LENGTH_SHORT)
    }

    override fun showMessage(charSequence: CharSequence) {
        toast.setText(charSequence)
        toast.show()
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