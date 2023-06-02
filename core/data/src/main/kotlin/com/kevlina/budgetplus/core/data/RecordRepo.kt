package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.Record
import java.time.LocalDate

interface RecordRepo {

    fun createRecord(record: Record)

    fun batchRecord(
        record: Record,
        startDate: LocalDate,
        frequency: BatchFrequency,
        times: Int,
    )

    fun editRecord(
        oldRecord: Record,
        newDate: LocalDate,
        newName: String,
        newPriceText: String,
    )

    /**
     *  @return How many records were edited.
     */
    suspend fun editBatch(
        oldRecord: Record,
        newDate: LocalDate,
        newName: String,
        newPriceText: String,
    ): Int

    fun deleteRecord(recordId: String)

    /**
     *  @return How many records were deleted.
     */
    suspend fun deleteBatch(record: Record): Int

    /**
     *  @param query The prefix of [Record.name] or the [Record.price].
     */
    suspend fun searchRecords(query: String): List<Record>

}