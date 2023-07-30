package com.kevlina.budgetplus.core.common

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import timber.log.Timber
import kotlin.contracts.contract

class CrashReportingTree : Timber.Tree() {

    private val crashlytics by lazy { Firebase.crashlytics }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        crashlytics.log(message)

        if (t.shouldRecordException() && priority >= Log.ERROR) {
            // Weird compiler error, the contract should work fine
            crashlytics.recordException(t!!)
        }
    }

    private fun Throwable?.shouldRecordException(): Boolean {
        contract {
            returns(true) implies (this@shouldRecordException != null)
        }
        return this != null && this !is CancellationException
    }
}