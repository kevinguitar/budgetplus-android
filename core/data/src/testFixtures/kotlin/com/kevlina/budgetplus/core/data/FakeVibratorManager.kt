package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.common.VibratorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeVibratorManager(
    initialVibrateOnInput: Boolean = true,
) : VibratorManager {

    override val vibrateOnInput: StateFlow<Boolean>
        field = MutableStateFlow(initialVibrateOnInput)

    override fun vibrate() = Unit

    override fun toggleVibrateOnInput() {
        vibrateOnInput.value = !vibrateOnInput.value
    }
}