package com.kevlina.budgetplus.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface Preference {

    val pref: SharedPreferences
    val editor: SharedPreferences.Editor

    fun clearAll()

}

@Singleton
class AppPreference @Inject constructor(
    @ApplicationContext context: Context
) : Preference {

    override val pref: SharedPreferences = context.getSharedPreferences(
        "app_preference",
        Context.MODE_PRIVATE
    )

    override val editor: SharedPreferences.Editor get() = pref.edit()

    override fun clearAll() {
        pref.edit().clear().apply()
    }
}