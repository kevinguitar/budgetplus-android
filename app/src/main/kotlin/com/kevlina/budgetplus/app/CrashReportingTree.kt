package com.kevlina.budgetplus.app

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import timber.log.Timber

class CrashReportingTree : Timber.Tree() {

    private val crashlytics by lazy { Firebase.crashlytics }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        crashlytics.log(message)

        if (priority >= Log.ERROR && t !is CancellationException) {
            // If t is null, wrap the message with an exception
            crashlytics.recordException(t ?: Exception(message))
        }
    }
}