package com.kevlina.budgetplus.feature.settings

interface SettingsNavigation {

    fun openLanguageSettings()

    suspend fun contactUs()

    fun visitUrl(url: String)

}