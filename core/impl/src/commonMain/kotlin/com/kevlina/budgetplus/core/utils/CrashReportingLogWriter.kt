package com.kevlina.budgetplus.core.utils

import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin
import co.touchlab.kermit.DefaultFormatter
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Message
import co.touchlab.kermit.Severity
import co.touchlab.kermit.Tag
import kotlinx.coroutines.CancellationException

class CrashReportingLogWriter : LogWriter() {

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        CrashlyticsKotlin.implementation.logMessage(
            DefaultFormatter.formatMessage(null, Tag(tag), Message(message))
        )

        if (severity >= Severity.Error && throwable !is CancellationException) {
            // If throwable is null, wrap the message with an exception
            CrashlyticsKotlin.implementation.sendHandledException(throwable ?: Exception(message))
        }
    }
}
