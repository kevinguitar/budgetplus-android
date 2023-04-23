package com.kevlina.budgetplus.core.common

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class CrashReportingTree : Timber.Tree() {

    private val crashlytics by lazy { Firebase.crashlytics }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        crashlytics.log(message)

        if (t != null && priority >= Log.ERROR) {
            crashlytics.recordException(t)
        }
    }
}