package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.impl.AppPreference
import com.kevlina.budgetplus.core.data.impl.AuthManagerImpl
import com.kevlina.budgetplus.core.data.impl.BookRepoImpl
import com.kevlina.budgetplus.core.data.impl.RecordRepoImpl
import com.kevlina.budgetplus.core.data.local.Preference
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
    fun provideBookRepo(impl: BookRepoImpl): BookRepo

    @Binds
    fun providePreference(impl: AppPreference): Preference

    @Binds
    fun provideRecordRepo(impl: RecordRepoImpl): RecordRepo
}