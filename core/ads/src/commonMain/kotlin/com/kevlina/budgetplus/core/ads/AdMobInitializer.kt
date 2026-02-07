package com.kevlina.budgetplus.core.ads

import kotlinx.coroutines.flow.StateFlow

interface AdMobInitializer {

    val isInitialized: StateFlow<Boolean>

    fun initialize()

    suspend fun ensureInitialized()

}