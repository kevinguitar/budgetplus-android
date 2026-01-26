package com.kevlina.budgetplus.core.data.local

import android.content.SharedPreferences

//TODO: Migrate to DataStore
interface Preference {

    val pref: SharedPreferences
    val editor: SharedPreferences.Editor

    fun clearAll()

}