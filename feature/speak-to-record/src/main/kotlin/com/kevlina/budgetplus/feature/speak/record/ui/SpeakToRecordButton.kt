package com.kevlina.budgetplus.feature.speak.record.ui

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.hasPermission
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.InfiniteCircularProgress
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.rippleIndication
import com.kevlina.budgetplus.core.ui.thenIf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ColumnScope.SpeakToRecordButton(
    state: SpeakToRecordButtonState,
    isAdaptive: Boolean,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val showLoader by state.showLoader.collectAsStateWithLifecycle()
    val showRecordingDialog by state.showRecordingDialog.collectAsStateWithLifecycle()

    val activity = LocalContext.current as Activity
    val recordPermission = Manifest.permission.RECORD_AUDIO
    val permissionRequester = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) state.showRecordPermissionHint()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .background(LocalAppColors.current.dark)
            .weight(1F)
            .thenIf(isAdaptive) { Modifier.fillMaxWidth() }
            .thenIf(!isAdaptive) { Modifier.aspectRatio(1F) }
            .rippleIndication(interactionSource)
            .onPlaced {
                state.highlightRecordButton(
                    BubbleDest.SpeakToRecord(
                        size = it.size,
                        offset = it.positionInRoot()
                    )
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        if (activity.hasPermission(recordPermission)) {
                            // If it's currently loading, do not trigger the tap again
                            if (showLoader) return@detectTapGestures

                            state.onTap()
                            val press = PressInteraction.Press(offset)
                            interactionSource.emit(press)

                            tryAwaitRelease()

                            state.onReleased()
                            interactionSource.emit(PressInteraction.Release(press))
                        } else {
                            permissionRequester.launch(recordPermission)
                        }
                    }
                )
            }
    ) {
        if (showLoader) {
            InfiniteCircularProgress(
                color = LocalAppColors.current.light,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Mic,
                tint = LocalAppColors.current.light
            )
        }
    }

    if (showRecordingDialog) {
        SpeakToRecordDialog()
    }
}

@Stable
data class SpeakToRecordButtonState(
    val onTap: () -> Unit,
    val onReleased: () -> Unit,
    val showLoader: StateFlow<Boolean>,
    val showRecordingDialog: StateFlow<Boolean>,
    val highlightRecordButton: (BubbleDest) -> Unit,
    val showRecordPermissionHint: () -> Unit,
) {
    companion object {
        val preview = SpeakToRecordButtonState(
            onTap = {},
            onReleased = {},
            showLoader = MutableStateFlow(false),
            showRecordingDialog = MutableStateFlow(false),
            highlightRecordButton = {},
            showRecordPermissionHint = {},
        )
    }
}

@Preview
@Composable
private fun SpeakToRecordButton_Preview() = AppTheme {
    Column {
        SpeakToRecordButton(
            state = SpeakToRecordButtonState.preview,
            isAdaptive = false
        )
        SpeakToRecordButton(
            state = SpeakToRecordButtonState.preview.copy(
                showLoader = MutableStateFlow(true)
            ),
            isAdaptive = false
        )
    }
}