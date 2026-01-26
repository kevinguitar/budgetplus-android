package com.kevlina.budgetplus.core.data

import kotlinx.coroutines.flow.StateFlow

interface RemoteConfig {

    fun getString(key: String, defaultValue: String): String

    fun getInt(key: String, defaultValue: Int): Int

    fun observeString(key: String, defaultValue: String): StateFlow<String>

    fun observeInt(key: String, defaultValue: Int): StateFlow<Int>

}