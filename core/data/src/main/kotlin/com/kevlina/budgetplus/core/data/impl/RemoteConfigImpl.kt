package com.kevlina.budgetplus.core.data.impl

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.data.RemoteConfig
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
            } catch (e: Exception) {
                Timber.e(e, "Remote config fetch failed")
            }
        }
    }

    override fun observeString(key: String, defaultValue: String): StateFlow<String> {
        return allConfigs.mapState {
            it[key]?.asString() ?: run {
                Timber.e("RemoteConfig: No config found, fallback to $defaultValue")
                defaultValue
            }
        }
    }
}