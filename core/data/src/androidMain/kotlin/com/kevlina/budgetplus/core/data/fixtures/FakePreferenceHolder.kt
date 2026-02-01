package com.kevlina.budgetplus.core.data.fixtures

import androidx.annotation.VisibleForTesting
import androidx.datastore.preferences.core.Preferences
import com.kevlina.budgetplus.core.data.local.Preference
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

@VisibleForTesting
@Suppress("FunctionName")
fun FakePreferenceHolder(mapBuilder: MutableMap<String, Any?>.() -> Unit): PreferenceHolder {
    return PreferenceHolder(FakePreference(mapBuilder), Json)
}

private class FakePreference(
    mapBuilder: MutableMap<String, Any?>.() -> Unit,
) : Preference {

    private val map = mutableMapOf<String, Any?>().apply(mapBuilder)

    override fun <T> of(key: Preferences.Key<T>): Flow<T?> {
        TODO("Not yet implemented")
    }

    override fun <T> of(key: Preferences.Key<T>, default: T, scope: CoroutineScope): StateFlow<T> {
        TODO("Not yet implemented")
    }

    override fun <T> of(key: Preferences.Key<String>, serializer: KSerializer<T>): Flow<T?> {
        TODO("Not yet implemented")
    }

    override fun <T> of(key: Preferences.Key<String>, serializer: KSerializer<T>, default: T, scope: CoroutineScope): StateFlow<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> update(key: Preferences.Key<T>, value: T) {
        TODO("Not yet implemented")
    }

    override suspend fun <T> update(key: Preferences.Key<String>, serializer: KSerializer<T>, value: T) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(key: Preferences.Key<*>) {
        TODO("Not yet implemented")
    }

    override suspend fun clearAll() {
        TODO("Not yet implemented")
    }
}