package com.kevlina.budgetplus.core.data.local

import android.content.SharedPreferences
import kotlinx.serialization.json.Json

@Suppress("FunctionName")
fun FakePreferenceHolder(mapBuilder: MutableMap<String, Any?>.() -> Unit): PreferenceHolder {
    return PreferenceHolder(FakePreference(mapBuilder), Json)
}

private class FakePreference(
    mapBuilder: MutableMap<String, Any?>.() -> Unit,
) : Preference {

    val map = mutableMapOf<String, Any?>().apply(mapBuilder)

    override val pref: SharedPreferences = object : SharedPreferences {
        override fun getAll(): MutableMap<String, *> = map
        override fun getString(key: String?, defValue: String?): String? = map[key] as String?
        override fun getStringSet(key: String?, defValues: MutableSet<String>?) = error("Not supported")
        override fun getInt(key: String?, defValue: Int): Int = map[key] as Int
        override fun getLong(key: String?, defValue: Long): Long = map[key] as Long
        override fun getFloat(key: String?, defValue: Float): Float = map[key] as Float
        override fun getBoolean(key: String?, defValue: Boolean): Boolean = map[key] as Boolean
        override fun contains(key: String?): Boolean = key in map
        override fun edit(): SharedPreferences.Editor = editor
        override fun registerOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?,
        ) = error("Not supported")

        override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?,
        ) = error("Not supported")
    }


    override val editor: SharedPreferences.Editor = object : SharedPreferences.Editor {
        override fun putString(key: String, value: String?): SharedPreferences.Editor = apply { map[key] = value }
        override fun putStringSet(key: String, values: MutableSet<String>?) = apply { map[key] = values }
        override fun putInt(key: String, value: Int): SharedPreferences.Editor = apply { map[key] = value }
        override fun putLong(key: String, value: Long): SharedPreferences.Editor = apply { map[key] = value }
        override fun putFloat(key: String, value: Float): SharedPreferences.Editor = apply { map[key] = value }
        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor = apply { map[key] = value }
        override fun remove(key: String): SharedPreferences.Editor = apply { map.remove(key) }
        override fun clear(): SharedPreferences.Editor = apply { map.clear() }
        override fun commit(): Boolean = true
        override fun apply() = Unit
    }

    override fun clearAll() {
        map.clear()
    }
}