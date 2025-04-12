package com.kevlina.budgetplus.core.inapp.update

import com.kevlina.budgetplus.inapp.update.InAppUpdateManager
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