package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.impl.AppPreference
import com.kevlina.budgetplus.core.data.impl.AuthManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    fun provideAuthManager(impl: AuthManagerImpl): AuthManager

    @Binds
    fun providePreference(impl: AppPreference): Preference
}