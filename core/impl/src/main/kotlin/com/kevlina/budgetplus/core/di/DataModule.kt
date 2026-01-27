package com.kevlina.budgetplus.core.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlin.time.Instant

@ContributesTo(AppScope::class)
interface DataModule {

    @Provides
    @SingleIn(AppScope::class)
    fun provideJson(): Json = Json {
        serializersModule = SerializersModule {
            contextual(LocalDateSerializer)
            contextual(LocalDateTimeSerializer)
            ignoreUnknownKeys = true
        }
    }
}

//TODO: Try and use default serializers!
private object LocalDateSerializer : KSerializer<LocalDate> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeLong(value.toEpochDays())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.fromEpochDays(decoder.decodeLong())
    }
}

private object LocalDateTimeSerializer : KSerializer<LocalDateTime> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeLong(value.toInstant(TimeZone.UTC).epochSeconds)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return Instant.fromEpochSeconds(decoder.decodeLong()).toLocalDateTime(TimeZone.UTC)
    }
}