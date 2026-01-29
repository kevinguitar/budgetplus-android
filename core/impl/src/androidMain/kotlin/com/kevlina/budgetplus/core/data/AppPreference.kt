package com.kevlina.budgetplus.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.kevlina.budgetplus.core.data.local.Preference
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AppPreference(context: Context) : Preference {

    override val pref: SharedPreferences = context.getSharedPreferences(
        "app_preference",
        Context.MODE_PRIVATE
    )

    override val editor: SharedPreferences.Editor get() = pref.edit()

    override fun clearAll() {
        pref.edit { clear() }
    }
}