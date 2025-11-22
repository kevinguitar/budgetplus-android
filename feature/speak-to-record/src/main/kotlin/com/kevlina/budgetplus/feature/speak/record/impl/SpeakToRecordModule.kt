package com.kevlina.budgetplus.feature.speak.record.impl

import com.kevlina.budgetplus.feature.speak.record.SpeakToRecord
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

@Module
@ContributesTo(AppScope::class)
@InstallIn(SingletonComponent::class)
interface SpeakToRecordModule {

    @Binds
    fun provideSpeakToRecord(impl: SpeakToRecordImpl): SpeakToRecord
}