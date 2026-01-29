package com.kevlina.budgetplus.core.data

import android.content.Context
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.VibratorManager
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class VibratorManagerImpl(
    context: Context,
    preferenceHolder: PreferenceHolder,
    private val tracker: Tracker,
) : VibratorManager {

    private var vibrateOnInputCache by preferenceHolder.bindBoolean(true)
    final override val vibrateOnInput: StateFlow<Boolean>
        field = MutableStateFlow(vibrateOnInputCache)

    override fun toggleVibrateOnInput() {
        val newValue = !vibrateOnInputCache
        vibrateOnInputCache = newValue
        vibrateOnInput.value = newValue
        tracker.logEvent("vibrate_on_input_changed")
    }
}