package com.kevingt.moneybook.di

import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.auth.AuthManagerImpl
import com.kevingt.moneybook.utils.AppCoroutineScope
import com.kevingt.moneybook.utils.AppScope
import com.kevingt.moneybook.utils.Toaster
import com.kevingt.moneybook.utils.ToasterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
interface GlobalModule {

    @Binds
    fun provideToaster(impl: ToasterImpl): Toaster

    @Binds
    fun provideAuthManager(impl: AuthManagerImpl): AuthManager

    companion object {

        @Provides
        @AppScope
        fun provideAppCoroutineScope(): CoroutineScope = AppCoroutineScope
    }
}