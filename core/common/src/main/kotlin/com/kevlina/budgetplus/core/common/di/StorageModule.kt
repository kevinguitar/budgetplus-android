package com.kevlina.budgetplus.core.common.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    @Named("share_cache")
    fun provideShareCacheDir(@ApplicationContext context: Context): File {
        return File(context.cacheDir, "share").apply { mkdirs() }
    }
}