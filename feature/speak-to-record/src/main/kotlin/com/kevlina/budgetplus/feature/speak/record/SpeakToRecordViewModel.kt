package com.kevlina.budgetplus.feature.speak.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.VibratorManager
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.ui.SnackbarSender
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class SpeakToRecordViewModel @Inject constructor(
    private val speakToRecord: SpeakToRecord,
    private val snackbarSender: SnackbarSender,
    private val vibratorManager: VibratorManager,
    private val bubbleRepo: BubbleRepo,
    preferenceHolder: PreferenceHolder,
) : ViewModel() {

    private val _speakResultFlow = MutableEventFlow<SpeakToRecordStatus.Success>()
    val speakResultFlow: EventFlow<SpeakToRecordStatus.Success> = _speakResultFlow.asStateFlow()

    private val _dismissDialogEvent = MutableEventFlow<Unit>()
    val dismissDialogEvent: EventFlow<Unit> = _dismissDialogEvent.asStateFlow()

    private var isSpeakToRecordBubbleShown by preferenceHolder.bindBoolean(false)

    private var recordActor: RecordActor? = null
    private var recordStatusJob: Job? = null

    fun onButtonTap() {
        recordStatusJob?.cancel()

        val actor = speakToRecord.startRecording()
        recordActor = actor
        recordStatusJob = actor.statusFlow
            .onEach(::handleResult)
            .launchIn(viewModelScope)
    }

    fun onButtonReleased() {
        recordActor?.stopRecording?.invoke()
        recordActor = null
    }

    fun highlightRecordButton(dest: BubbleDest) {
        if (!isSpeakToRecordBubbleShown) {
            isSpeakToRecordBubbleShown = true
            bubbleRepo.addBubbleToQueue(dest)
        }
    }

    fun showRecordPermissionHint() {
        snackbarSender.send(R.string.permission_hint, canDismiss = true)
    }

    private fun handleResult(result: SpeakToRecordStatus) {
        // If record recognizer respond earlier than the user releases their finger, dismiss the dialog
        // to hint the user that the current round of recording is over.
        if (result !is SpeakToRecordStatus.ReadyToSpeak) {
            _dismissDialogEvent.sendEvent()
        }

        when (result) {
            SpeakToRecordStatus.DeviceNotSupported -> {
                snackbarSender.send(R.string.record_speech_recognition_not_supported, canDismiss = true)
            }

            SpeakToRecordStatus.ReadyToSpeak -> vibratorManager.vibrate()

            is SpeakToRecordStatus.Error -> snackbarSender.send(result.message, canDismiss = true)

            SpeakToRecordStatus.NoResult -> {
                snackbarSender.send(R.string.record_speech_recognition_no_result, canDismiss = true)
            }

            is SpeakToRecordStatus.Success -> {
                _speakResultFlow.sendEvent(result)
            }
        }
    }
}