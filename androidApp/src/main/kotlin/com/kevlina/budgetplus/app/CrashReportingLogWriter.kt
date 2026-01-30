package com.kevlina.budgetplus.app

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.CancellationException

class CrashReportingLogWriter : LogWriter() {

    private val crashlytics by lazy { Firebase.crashlytics }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        crashlytics.log(message)

        if (severity >= Severity.Error && throwable !is CancellationException) {
            // If throwable is null, wrap the message with an exception
            crashlytics.recordException(throwable ?: Exception(message))
        }
    }
}
