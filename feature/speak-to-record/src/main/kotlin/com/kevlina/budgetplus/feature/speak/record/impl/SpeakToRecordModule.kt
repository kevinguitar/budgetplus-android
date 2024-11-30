package com.kevlina.budgetplus.feature.speak.record.impl

import com.kevlina.budgetplus.feature.speak.record.SpeakToRecord
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SpeakToRecordModule {

    @Binds
    fun provideSpeakToRecord(impl: SpeakToRecordImpl): SpeakToRecord
}