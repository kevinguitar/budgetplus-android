package com.kevingt.moneybook.data.local

import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceHolder @Inject constructor(
    val preference: Preference,
    val gson: Gson,
) {

    fun bindString(default: String?): StringPreferenceDelegate {
        return StringPreferenceDelegate(preference, default)
    }

    fun bindInt(default: Int): IntPreferenceDelegate {
        return IntPreferenceDelegate(preference, default)
    }

    fun bindBoolean(default: Boolean): BooleanPreferenceDelegate {
        return BooleanPreferenceDelegate(preference, default)
    }

    inline fun <reified T : Any?> bindObjectOptional(default: T? = null) =
        OptionalParcelablePreferenceDelegate(default, T::class.java, preference, gson)

    inline fun <reified T : Any> bindObject(default: T) =
        ParcelablePreferenceDelegate(default, T::class.java, preference, gson)

}