package com.kevlina.budgetplus.core.data

import kotlinx.coroutines.flow.StateFlow

interface RemoteConfig {

    fun observeString(key: String, defaultValue: String): StateFlow<String>

}