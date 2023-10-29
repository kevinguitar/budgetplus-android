package com.kevlina.budgetplus.core.data

import kotlinx.coroutines.flow.StateFlow

interface VibratorManager {

    val vibrateOnInput: StateFlow<Boolean>

    fun vibrate()

    fun toggleVibrateOnInput()
}