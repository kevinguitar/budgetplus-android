package com.kevingt.moneybook.data.local

import com.google.gson.Gson
import timber.log.Timber
import kotlin.reflect.KProperty

/**
 * Preference Delegate classes
 */
class StringPreferenceDelegate(
    private val preference: Preference, private val default: String?
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        return preference.pref.getString(property.name, default) ?: default
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        preference.editor.putString(property.name, value).apply()
    }
}

class IntPreferenceDelegate(
    private val preference: Preference, private val default: Int
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return preference.pref.getInt(property.name, default)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        preference.editor.putInt(property.name, value).apply()
    }
}

class BooleanPreferenceDelegate(
    private val preference: Preference, private val default: Boolean
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return preference.pref.getBoolean(property.name, default)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        preference.editor.putBoolean(property.name, value).apply()
    }
}

class OptionalParcelablePreferenceDelegate<T : Any?>(
    private val default: T?,
    private val clazz: Class<T>,
    private val preference: Preference,
    private val gson: Gson,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        val json = preference.pref.getString(property.name, null) ?: return default
        return try {
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            Timber.e(e)
            default
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        try {
            val json = gson.toJson(value)
            preference.editor.putString(property.name, json).apply()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}

class ParcelablePreferenceDelegate<T : Any>(
    private val default: T,
    private val clazz: Class<T>,
    private val preference: Preference,
    private val gson: Gson,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val json = preference.pref.getString(property.name, null) ?: return default
        return try {
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            Timber.e(e)
            default
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        try {
            val json = gson.toJson(value)
            preference.editor.putString(property.name, json).apply()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}