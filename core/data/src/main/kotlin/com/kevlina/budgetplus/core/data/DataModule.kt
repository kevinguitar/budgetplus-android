package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.impl.AppPreference
import com.kevlina.budgetplus.core.data.impl.AuthManagerImpl
import com.kevlina.budgetplus.core.data.impl.BookRepoImpl
import com.kevlina.budgetplus.core.data.impl.RecordRepoImpl
import com.kevlina.budgetplus.core.data.impl.RecordsObserverImpl
import com.kevlina.budgetplus.core.data.impl.TrackerImpl
import com.kevlina.budgetplus.core.data.impl.UserRepoImpl
import com.kevlina.budgetplus.core.data.local.Preference
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.time.LocalDate
import javax.inject.Singleton

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

    @Binds
    fun provideUserRepo(impl: UserRepoImpl): UserRepo

    @Binds
    fun provideTracker(impl: TrackerImpl): Tracker

    @Binds
    fun provideRecordsObserver(impl: RecordsObserverImpl): RecordsObserver

    companion object {

        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            serializersModule = SerializersModule {
                contextual(LocalDateSerializer)
            }
        }
    }
}

private object LocalDateSerializer : KSerializer<LocalDate> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeLong(value.toEpochDay())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.ofEpochDay(decoder.decodeLong())
    }
}