package com.kevlina.budgetplus.core.data.local

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Singleton
class PreferenceHolder @Inject constructor(
    val preference: Preference,
    val formatter: Json,
) {

    fun bindInt(default: Int) = object : ReadWriteProperty<Any, Int> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Int {
            return preference.pref.getInt(property.name, default)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
            preference.editor.putInt(property.name, value).apply()
        }
    }

    fun bindLong(default: Long) = object : ReadWriteProperty<Any, Long> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Long {
            return preference.pref.getLong(property.name, default)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
            preference.editor.putLong(property.name, value).apply()
        }
    }

    fun bindBoolean(default: Boolean) = object : ReadWriteProperty<Any, Boolean> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
            return preference.pref.getBoolean(property.name, default)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
            preference.editor.putBoolean(property.name, value).apply()
        }
    }

    inline fun <reified T : Any> bindObject(default: T) = object : ReadWriteProperty<Any, T> {

        override operator fun getValue(thisRef: Any, property: KProperty<*>): T {
            val json = preference.pref.getString(property.name, null) ?: return default
            return try {
                formatter.decodeFromString(json)
            } catch (e: Exception) {
                Timber.e(e)
                default
            }
        }

        override operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            try {
                val json = formatter.encodeToString(value)
                preference.editor.putString(property.name, json).apply()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    inline fun <reified T : Any?> bindObjectOptional(
        default: T? = null
    ) = object : ReadWriteProperty<Any, T?> {

        override operator fun getValue(thisRef: Any, property: KProperty<*>): T? {
            val json = preference.pref.getString(property.name, null) ?: return default
            return try {
                formatter.decodeFromString(json)
            } catch (e: Exception) {
                Timber.e(e)
                default
            }
        }

        override operator fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
            try {
                val json = formatter.encodeToString(value)
                preference.editor.putString(property.name, json).apply()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}