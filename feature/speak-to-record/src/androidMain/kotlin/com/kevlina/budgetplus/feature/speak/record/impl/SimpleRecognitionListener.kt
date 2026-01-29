package com.kevlina.budgetplus.feature.speak.record.impl

import android.os.Bundle
import android.speech.RecognitionListener

abstract class SimpleRecognitionListener : RecognitionListener {
    override fun onBeginningOfSpeech() = Unit
    override fun onRmsChanged(v: Float) = Unit
    override fun onBufferReceived(bytes: ByteArray) = Unit
    override fun onEndOfSpeech() = Unit
    override fun onEvent(i: Int, bundle: Bundle) = Unit
    override fun onPartialResults(results: Bundle) = Unit
}