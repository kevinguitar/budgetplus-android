package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.Record
import kotlinx.coroutines.Job
import kotlinx.datetime.LocalDate

object FakeRecordRepo : RecordRepo {

    var lastCreatedRecord: Record? = null

    override fun createRecord(record: Record) {
        lastCreatedRecord = record
    }

    override fun batchRecord(record: Record, startDate: LocalDate, frequency: BatchFrequency, times: Int): String {
        error("Not yet implemented")
    }

    override fun editRecord(
        oldRecord: Record,
        newDate: LocalDate,
        newCategory: String,
        newName: String,
        newPriceText: String,
    ) {
        error("Not yet implemented")
    }

    override suspend fun editBatch(
        oldRecord: Record,
        newDate: LocalDate,
        newCategory: String,
        newName: String,
        newPriceText: String,
    ): Int {
        error("Not yet implemented")
    }

    override fun duplicateRecord(record: Record) {
        error("Not yet implemented")
    }

    override fun deleteRecord(recordId: String) {
        error("Not yet implemented")
    }

    override suspend fun deleteBatch(record: Record): Int {
        error("Not yet implemented")
    }

    override fun renameCategories(events: List<CategoryRenameEvent>): Job {
        error("Not yet implemented")
    }
}