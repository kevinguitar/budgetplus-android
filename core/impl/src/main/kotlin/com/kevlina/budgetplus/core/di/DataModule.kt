package com.kevlina.budgetplus.core.di

import com.kevlina.budgetplus.core.common.AppStartAction
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.VibratorManager
import com.kevlina.budgetplus.core.data.AppPreference
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.AuthManagerImpl
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.BookRepoImpl
import com.kevlina.budgetplus.core.data.InsiderRepo
import com.kevlina.budgetplus.core.data.InsiderRepoImpl
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.RecordRepoImpl
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.RecordsObserverImpl
import com.kevlina.budgetplus.core.data.RemoteConfig
import com.kevlina.budgetplus.core.data.RemoteConfigImpl
import com.kevlina.budgetplus.core.data.TrackerImpl
import com.kevlina.budgetplus.core.data.UserRepo
import com.kevlina.budgetplus.core.data.UserRepoImpl
import com.kevlina.budgetplus.core.data.VibratorManagerImpl
import com.kevlina.budgetplus.core.data.local.Preference
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
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
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    fun bindAuthManager(impl: AuthManagerImpl): AuthManager

    @Binds
    fun bindVibratorManager(impl: VibratorManagerImpl): VibratorManager

    @Binds
    fun bindBookRepo(impl: BookRepoImpl): BookRepo

    @Binds
    fun bindPreference(impl: AppPreference): Preference

    @Binds
    fun bindRecordRepo(impl: RecordRepoImpl): RecordRepo

    @Binds
    fun bindUserRepo(impl: UserRepoImpl): UserRepo

    @Binds
    fun bindInsiderRepo(impl: InsiderRepoImpl): InsiderRepo

    @Binds @IntoSet
    fun bindUserRepoOnAppStart(impl: UserRepoImpl): AppStartAction

    @Binds
    fun bindRemoteConfig(impl: RemoteConfigImpl): RemoteConfig

    @Binds
    fun bindTracker(impl: TrackerImpl): Tracker

    @Binds
    fun bindRecordsObserver(impl: RecordsObserverImpl): RecordsObserver

    companion object {

        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            serializersModule = SerializersModule {
                contextual(LocalDateSerializer)
                contextual(LocalDateTimeSerializer)
                ignoreUnknownKeys = true
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

private object LocalDateTimeSerializer : KSerializer<LocalDateTime> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeLong(value.toEpochSecond(ZoneOffset.UTC))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.ofEpochSecond(decoder.decodeLong(), 0, ZoneOffset.UTC)
    }
}