package com.kevlina.budgetplus.core.common

import dev.icerock.moko.permissions.ios.PermissionsController
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface OpenAppSettingsActionProvider {

    @Provides
    fun provideOpenSettingsAction(): OpenAppSettingsAction = {
        PermissionsController().openAppSettings()
    }
}