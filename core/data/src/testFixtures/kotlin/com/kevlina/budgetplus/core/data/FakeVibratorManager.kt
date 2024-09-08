package com.kevlina.budgetplus.core.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeVibratorManager(
    initialVibrateOnInput: Boolean = true,
) : VibratorManager {

    private val _vibrateOnInput = MutableStateFlow(initialVibrateOnInput)
    override val vibrateOnInput: StateFlow<Boolean> = _vibrateOnInput.asStateFlow()

    override fun vibrate() = Unit

    override fun toggleVibrateOnInput() {
        _vibrateOnInput.value = !vibrateOnInput.value
    }
}