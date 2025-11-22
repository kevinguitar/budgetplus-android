package com.kevlina.budgetplus.core.di

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import java.io.File

@ContributesTo(AppScope::class)
interface StorageModule {

    @Provides
    @SingleIn(AppScope::class)
    @Named("share_cache")
    fun provideShareCacheDir(context: Context): File {
        return File(context.cacheDir, "share").apply { mkdirs() }
    }
}