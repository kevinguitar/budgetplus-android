package com.kevlina.budgetplus.feature.speak.record

import android.speech.SpeechRecognizer

sealed interface SpeakToRecordStatus {

    data object DeviceNotSupported : SpeakToRecordStatus

    data object ReadyToSpeak : SpeakToRecordStatus

    data object Recognizing : SpeakToRecordStatus

    data object NoResult : SpeakToRecordStatus

    data class Error(val message: String) : SpeakToRecordStatus

    data class Success(
        val name: String,
        val price: Double?,
    ) : SpeakToRecordStatus

    companion object
}

internal fun SpeakToRecordStatus.Companion.fromErrorCode(code: Int): SpeakToRecordStatus {
    val message = when (code) {
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout error"
        SpeechRecognizer.ERROR_NETWORK -> "Network error"
        SpeechRecognizer.ERROR_CLIENT -> "Client error"
        SpeechRecognizer.ERROR_NO_MATCH -> return SpeakToRecordStatus.NoResult
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
        SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED -> "The requested language is not supported"
        else -> "Unknown error"
    }
    return SpeakToRecordStatus.Error(message)
}
