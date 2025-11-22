package com.kevlina.budgetplus.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
@ContributesTo(AppScope::class)
@InstallIn(SingletonComponent::class)
interface StorageModule {

    companion object {
        @Provides
        @Singleton
        @Named("share_cache")
        fun provideShareCacheDir(@ApplicationContext context: Context): File {
            return File(context.cacheDir, "share").apply { mkdirs() }
        }
    }
}