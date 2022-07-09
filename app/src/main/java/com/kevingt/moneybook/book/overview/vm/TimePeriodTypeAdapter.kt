package com.kevingt.moneybook.book.overview.vm

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate

class TimePeriodTypeAdapter : TypeAdapter<TimePeriod>() {

    override fun write(writer: JsonWriter, value: TimePeriod) {
        val periodValue = if (value is TimePeriod.Custom) {
            "${value.from.toEpochDay()},${value.until.toEpochDay()}"
        } else {
            value::class.simpleName.orEmpty()
        }

        with(writer) {
            beginObject()
            name("value")
            value(periodValue)
            endObject()
        }
    }

    override fun read(reader: JsonReader): TimePeriod {
        reader.beginObject()
        reader.nextName()
        val result = when (val periodRawValue = reader.nextString()) {
            TimePeriod.Today::class.simpleName -> TimePeriod.Today
            TimePeriod.Week::class.simpleName -> TimePeriod.Week
            TimePeriod.Month::class.simpleName -> TimePeriod.Month
            TimePeriod.LastMonth::class.simpleName -> TimePeriod.LastMonth
            else -> {
                val timestamps = periodRawValue.split(',')
                TimePeriod.Custom(
                    from = LocalDate.ofEpochDay(timestamps[0].toLong()),
                    until = LocalDate.ofEpochDay(timestamps[1].toLong()),
                )
            }
        }
        reader.endObject()
        return result
    }
}