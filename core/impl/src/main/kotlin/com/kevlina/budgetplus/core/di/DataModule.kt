package com.kevlina.budgetplus.core.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

@ContributesTo(AppScope::class)
interface DataModule {

    @Provides
    @SingleIn(AppScope::class)
    fun provideJson(): Json = Json {
        serializersModule = SerializersModule {
            ignoreUnknownKeys = true
        }
    }
}
