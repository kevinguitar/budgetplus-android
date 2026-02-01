package com.kevlina.budgetplus.core.data.local

import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.properties.ReadOnlyProperty

@SingleIn(AppScope::class)
@Inject
class PreferenceHolder(
    val preference: Preference,
    val formatter: Json,
) {
    fun bindString(default: String) = ReadOnlyProperty<Any, Flow<String>> { _, property ->
        val key = stringPreferencesKey(property.name)
        preference.data.map { it[key] ?: default }
    }

    suspend fun setString(name: String, value: String) {
        preference.update(stringPreferencesKey(name), value)
    }

    inline fun <reified T : Any> bindObject(default: T) = ReadOnlyProperty<Any, Flow<T>> { _, property ->
        val key = stringPreferencesKey(property.name)
        preference.data.map { prefs ->
            val json = prefs[key] ?: return@map default
            try {
                formatter.decodeFromString<T>(json)
            } catch (e: Exception) {
                Logger.e(e) { "Preference: Decode failed" }
                default
            }
        }
    }

    suspend inline fun <reified T : Any> setObject(name: String, value: T) {
        try {
            val json = formatter.encodeToString(value)
            preference.update(stringPreferencesKey(name), json)
        } catch (e: Exception) {
            Logger.e(e) { "Preference: Encode failed" }
        }
    }

}