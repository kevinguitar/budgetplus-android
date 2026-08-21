package com.kevlina.budgetplus.core.common

import android.content.Context
import dev.icerock.moko.permissions.PermissionsControllerImpl
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface OpenAppSettingsActionProvider {

    @Provides
    fun provideOpenSettingsAction(context: Context): OpenAppSettingsAction = {
        PermissionsControllerImpl(context).openAppSettings()
    }
}