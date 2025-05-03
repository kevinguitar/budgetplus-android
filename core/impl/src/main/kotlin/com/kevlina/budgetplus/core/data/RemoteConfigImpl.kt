package com.kevlina.budgetplus.core.data

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.mapState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

private typealias ConfigMap = Map<String, FirebaseRemoteConfigValue>

@Singleton
internal class RemoteConfigImpl @Inject constructor(
    @Named("is_debug") isDebug: Boolean,
    @AppScope private val appScope: CoroutineScope,
) : RemoteConfig {

    private val remoteConfig = Firebase.remoteConfig

    private val allConfigs = MutableStateFlow<ConfigMap>(emptyMap())

    init {
        if (isDebug) {
            // To be able to rapidly fetch config while debugging.
            remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
                @Suppress("MagicNumber")
                minimumFetchIntervalInSeconds = 3600
            })
        }

        fetchConfigs()
    }

    private fun fetchConfigs() {
        // Retrieve the cache
        allConfigs.value = remoteConfig.all

        appScope.launch {
            try {
                remoteConfig.fetchAndActivate().await()
                allConfigs.value = remoteConfig.all
                Timber.d("RemoteConfig: Configs fetched ${allConfigs.value.keys}")
            } catch (e: Exception) {
                Timber.w(e, "RemoteConfig: Fetch failed")
            }
        }
    }

    override fun getString(key: String, defaultValue: String): String {
        val value = allConfigs.value[key]?.asString()
        return if (value == null) {
            Timber.d("RemoteConfig: No string config found for $key, fallback to $defaultValue")
            defaultValue
        } else {
            Timber.d("RemoteConfig: Resolved $key to $value")
            value
        }
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        val value = allConfigs.value[key]?.asLong()?.toInt()
        return if (value == null) {
            Timber.d("RemoteConfig: No long config found for $key, fallback to $defaultValue")
            defaultValue
        } else {
            Timber.d("RemoteConfig: Resolved $key to $value")
            value
        }
    }

    override fun observeString(key: String, defaultValue: String): StateFlow<String> {
        return allConfigs.mapState { getString(key, defaultValue) }
    }

    override fun observeInt(key: String, defaultValue: Int): StateFlow<Int> {
        return allConfigs.mapState { getInt(key, defaultValue) }
    }
}