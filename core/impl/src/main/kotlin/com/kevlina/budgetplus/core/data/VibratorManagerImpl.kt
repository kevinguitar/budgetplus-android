package com.kevlina.budgetplus.core.data

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.VibratorManager
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import android.os.VibratorManager as AndroidVibratorManager

@Singleton
class VibratorManagerImpl @Inject constructor(
    @ApplicationContext context: Context,
    preferenceHolder: PreferenceHolder,
    private val tracker: Tracker,
) : VibratorManager {

    private var vibrateOnInputCache by preferenceHolder.bindBoolean(true)
    private val _vibrateOnInput = MutableStateFlow(vibrateOnInputCache)
    override val vibrateOnInput: StateFlow<Boolean> = _vibrateOnInput.asStateFlow()

    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as AndroidVibratorManager)
            .defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private val vibrationDuration get() = 2L

    override fun vibrate() {
        if (!vibrateOnInput.value) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(vibrationDuration)
        }
    }

    override fun toggleVibrateOnInput() {
        val newValue = !vibrateOnInputCache
        vibrateOnInputCache = newValue
        _vibrateOnInput.value = newValue

        if (newValue) {
            vibrate()
        }
        tracker.logEvent("vibrate_on_input_changed")
    }
}