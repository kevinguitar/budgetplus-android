package com.kevlina.budgetplus.core.common

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import timber.log.Timber

class CrashReportingTree : Timber.Tree() {

    private val crashlytics by lazy { Firebase.crashlytics }

    private val ignoreTypes = setOf(CancellationException::class)

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        crashlytics.log(message)

        if (t != null && priority >= Log.ERROR && t::class !in ignoreTypes) {
            crashlytics.recordException(t)
        }
    }
}