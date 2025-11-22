package com.kevlina.budgetplus.core.inapp.update

import com.kevlina.budgetplus.inapp.update.InAppUpdateManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

@Module
@ContributesTo(AppScope::class)
@InstallIn(ActivityComponent::class)
interface InAppUpdateModule {

    @Binds
    fun provideInAppUpdateManager(impl: InAppUpdateManagerImpl): InAppUpdateManager
}