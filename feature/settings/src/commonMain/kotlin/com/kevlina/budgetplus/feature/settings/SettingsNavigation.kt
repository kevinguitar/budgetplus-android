package com.kevlina.budgetplus.feature.settings

interface SettingsNavigation {

    fun openLanguageSettings(onLanguageChanged: (String) -> Unit)

    suspend fun contactUs()

    fun visitUrl(url: String)

}