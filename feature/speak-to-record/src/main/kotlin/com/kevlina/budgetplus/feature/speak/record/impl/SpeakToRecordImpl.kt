package com.kevlina.budgetplus.feature.speak.record.impl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.bundle
import com.kevlina.budgetplus.feature.speak.record.RecordActor
import com.kevlina.budgetplus.feature.speak.record.SpeakToRecord
import com.kevlina.budgetplus.feature.speak.record.SpeakToRecordStatus
import com.kevlina.budgetplus.feature.speak.record.fromErrorCode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject

internal class SpeakToRecordImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val stringProvider: StringProvider,
    private val speakResultParser: SpeakResultParser,
    private val tracker: Tracker,
) : SpeakToRecord {

    override val isAvailableOnDevice = SpeechRecognizer.isRecognitionAvailable(context)

    override fun startRecording(): RecordActor {
        if (!isAvailableOnDevice) {
            Timber.e("SpeechRecognizer: Feature is not supported")
            return RecordActor(
                statusFlow = flowOf(SpeakToRecordStatus.DeviceNotSupported),
                stopRecording = {}
            )
        }

        val statusFlow = MutableSharedFlow<SpeakToRecordStatus>(
            // Not acceptable to lose any events
            extraBufferCapacity = Int.MAX_VALUE,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer.setRecognitionListener(object : SimpleRecognitionListener() {
            override fun onReadyForSpeech(bundle: Bundle) {
                Timber.d("SpeechRecognizer: Ready for speech")
                statusFlow.tryEmit(SpeakToRecordStatus.ReadyToSpeak)
            }

            override fun onResults(results: Bundle) {
                recognizer.destroy()

                val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Timber.d("SpeechRecognizer: Results received $data")
                // First element is the most likely candidate.
                statusFlow.tryEmit(speakResultParser.parse(data?.firstOrNull()))
            }

            override fun onError(code: Int) {
                recognizer.destroy()

                Timber.e("SpeechRecognizer: Error $code")
                val status = SpeakToRecordStatus.fromErrorCode(code)
                statusFlow.tryEmit(status)

                if (status is SpeakToRecordStatus.Error) {
                    tracker.logEvent(
                        event = "speak_to_record_error",
                        params = bundle { putInt("code", code) }
                    )
                }
            }
        })

        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            .putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                stringProvider[R.string.record_speech_recognition_code]
            )

        Timber.d("SpeechRecognizer: Start listening")
        tracker.logEvent("speak_to_record_start")
        recognizer.startListening(recognizerIntent)

        return RecordActor(
            statusFlow = statusFlow,
            stopRecording = { recognizer.stopListening() }
        )
    }
}