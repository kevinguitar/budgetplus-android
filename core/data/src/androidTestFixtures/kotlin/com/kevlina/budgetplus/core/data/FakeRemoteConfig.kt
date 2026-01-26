package com.kevlina.budgetplus.core.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeRemoteConfig(
    private val configMap: Map<String, Any> = emptyMap(),
) : RemoteConfig {

    override fun getString(key: String, defaultValue: String): String {
        return configMap[key] as String? ?: defaultValue
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return configMap[key] as Int? ?: defaultValue
    }

    override fun observeString(key: String, defaultValue: String): StateFlow<String> {
        return MutableStateFlow(getString(key, defaultValue))
    }

    override fun observeInt(key: String, defaultValue: Int): StateFlow<Int> {
        return MutableStateFlow(getInt(key, defaultValue))
    }
}