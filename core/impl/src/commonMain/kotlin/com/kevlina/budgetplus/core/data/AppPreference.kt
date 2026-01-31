package com.kevlina.budgetplus.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.kevlina.budgetplus.core.data.local.Preference
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AppPreference(private val dataStore: DataStore<Preferences>) : Preference {

    override fun <T> of(key: Preferences.Key<T>): Flow<T?> {
        return dataStore.data.map { it[key] }
    }

    override fun <T> of(key: Preferences.Key<T>, default: T, scope: CoroutineScope): StateFlow<T> {
        return dataStore.data
            .map { it[key] ?: default }
            .stateIn(scope, SharingStarted.WhileSubscribed(), default)
    }

    override suspend fun <T> update(key: Preferences.Key<T>, value: T?) {
        dataStore.edit { prefs ->
            if (value == null) {
                prefs.remove(key)
            } else {
                prefs[key] = value
            }
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}

internal const val dataStoreFileName = "app_preference_v2"
