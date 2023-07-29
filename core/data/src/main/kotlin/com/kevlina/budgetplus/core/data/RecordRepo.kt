package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.Record
import kotlinx.coroutines.Job
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
        newCategory: String,
        newName: String,
        newPriceText: String,
    )

    /**
     *  @return How many records were edited.
     */
    suspend fun editBatch(
        oldRecord: Record,
        newDate: LocalDate,
        newCategory: String,
        newName: String,
        newPriceText: String,
    ): Int

    fun deleteRecord(recordId: String)

    /**
     *  @return How many records were deleted.
     */
    suspend fun deleteBatch(record: Record): Int

    fun renameCategories(events: List<CategoryRenameEvent>): Job

}

data class CategoryRenameEvent(
    val from: String,
    val to: String,
)