package com.kevlina.budgetplus.core.data

import android.content.SharedPreferences

interface Preference {

    val pref: SharedPreferences
    val editor: SharedPreferences.Editor

    fun clearAll()

}