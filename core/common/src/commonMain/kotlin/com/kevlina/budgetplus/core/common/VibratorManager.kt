package com.kevlina.budgetplus.core.common

import kotlinx.coroutines.flow.StateFlow

interface VibratorManager {

    val vibrateOnInput: StateFlow<Boolean>

    fun toggleVibrateOnInput()
}