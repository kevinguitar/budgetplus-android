package com.kevingt.moneybook.di

import com.google.gson.Gson
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.auth.AuthManagerImpl
import com.kevingt.moneybook.data.local.AppPreference
import com.kevingt.moneybook.data.local.Preference
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.BookRepoImpl
import com.kevingt.moneybook.data.remote.RecordRepo
import com.kevingt.moneybook.data.remote.RecordRepoImpl
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

    @Binds
    fun providePreference(impl: AppPreference): Preference

    @Binds
    fun provideBookRepo(impl: BookRepoImpl): BookRepo

    @Binds
    fun provideRecordRepo(impl: RecordRepoImpl): RecordRepo

    companion object {

        @Provides
        @AppScope
        fun provideAppCoroutineScope(): CoroutineScope = AppCoroutineScope

        @Provides
        fun provideGson(): Gson = Gson()
    }
}