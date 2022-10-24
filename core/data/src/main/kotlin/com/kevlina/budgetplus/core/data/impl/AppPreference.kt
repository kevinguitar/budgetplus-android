package com.kevlina.budgetplus.core.data.impl

import android.content.Context
import android.content.SharedPreferences
import com.kevlina.budgetplus.core.data.local.Preference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppPreference @Inject constructor(
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