package com.kevlina.budgetplus.inapp.update

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
internal interface InAppUpdateModule {

    @Binds
    fun provideInAppUpdateManager(impl: InAppUpdateManagerImpl): InAppUpdateManager
}