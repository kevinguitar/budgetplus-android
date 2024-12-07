package com.kevlina.budgetplus.feature.speak.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.VibratorManager
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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

    private var isSpeakToRecordBubbleShown by preferenceHolder.bindBoolean(false)

    private val recordActorFlow = MutableStateFlow<RecordActor?>(null)

    private val recordStatusFlow = recordActorFlow
        .flatMapLatest { it?.statusFlow ?: emptyFlow() }

    val showLoader: StateFlow<Boolean> = recordStatusFlow
        .map { it is SpeakToRecordStatus.ReadyToSpeak || it is SpeakToRecordStatus.Recognizing }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val showRecordingDialog: StateFlow<Boolean> = recordStatusFlow
        .map { it is SpeakToRecordStatus.ReadyToSpeak }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        recordStatusFlow
            .onEach(::handleStatus)
            .launchIn(viewModelScope)
    }

    fun onButtonTap() {
        recordActorFlow.value = speakToRecord.startRecording()
        vibratorManager.vibrate()
    }

    fun onButtonReleased() {
        recordActorFlow.value?.stopRecording?.invoke()
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

    private fun handleStatus(status: SpeakToRecordStatus) {
        when (status) {
            SpeakToRecordStatus.DeviceNotSupported -> {
                snackbarSender.send(R.string.record_speech_recognition_not_supported, canDismiss = true)
            }

            SpeakToRecordStatus.ReadyToSpeak, SpeakToRecordStatus.Recognizing -> Unit

            is SpeakToRecordStatus.Error -> snackbarSender.send(status.message, canDismiss = true)

            SpeakToRecordStatus.NoResult -> {
                snackbarSender.send(R.string.record_speech_recognition_no_result, canDismiss = true)
            }

            is SpeakToRecordStatus.Success -> {
                _speakResultFlow.sendEvent(status)
            }
        }
    }
}