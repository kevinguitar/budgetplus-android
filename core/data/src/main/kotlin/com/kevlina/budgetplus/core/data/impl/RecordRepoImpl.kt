package com.kevlina.budgetplus.core.data.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.parseToPrice
import com.kevlina.budgetplus.core.common.withCurrentTime
import com.kevlina.budgetplus.core.data.BatchFrequency
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.await
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.RecordsDb
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
internal class RecordRepoImpl @Inject constructor(
    @RecordsDb private val recordsDb: Provider<CollectionReference>,
    private val toaster: Toaster,
) : RecordRepo {

    override fun createRecord(record: Record) {
        recordsDb.get().add(record)
    }

    override fun batchRecord(
        record: Record,
        startDate: LocalDate,
        frequency: BatchFrequency,
        times: Int,
    ) {
        val batchId = UUID.randomUUID().toString()
        var currentDate = startDate

        repeat(times) {
            createRecord(record.copy(
                date = currentDate.toEpochDay(),
                timestamp = currentDate.withCurrentTime,
                batchId = batchId
            ))

            currentDate = when (frequency) {
                BatchFrequency.Monthly -> currentDate.plusMonths(1)
                BatchFrequency.Weekly -> currentDate.plusWeeks(1)
                BatchFrequency.Daily -> currentDate.plusDays(1)
            }
        }
    }

    override fun editRecord(
        oldRecord: Record,
        newDate: LocalDate,
        newName: String,
        newPriceText: String,
    ) {
        // Keep the record's original time
        val originalTime = LocalDateTime
            .ofEpochSecond(oldRecord.createdOn, 0, ZoneOffset.UTC)
            .toLocalTime()

        val newRecord = try {
            oldRecord.copy(
                date = newDate.toEpochDay(),
                timestamp = LocalDateTime.of(newDate, originalTime).toEpochSecond(ZoneOffset.UTC),
                name = newName,
                price = newPriceText.parseToPrice
            )
        } catch (e: Exception) {
            toaster.showError(e)
            return
        }

        recordsDb.get().document(oldRecord.id).set(newRecord)
    }

    override suspend fun editBatch(
        oldRecord: Record,
        newDate: LocalDate,
        newName: String,
        newPriceText: String,
    ): Int {
        // If record isn't batched for some reason, simply delete it.
        if (!oldRecord.isBatched) {
            editRecord(oldRecord, newDate, newName, newPriceText)
            return 1
        }

        val oldDate = LocalDate.ofEpochDay(oldRecord.date)
        val daysDiff = ChronoUnit.DAYS.between(oldDate, newDate)

        val records = getAllTheFutureRecords(oldRecord)
        records.forEach { doc ->
            val record = doc.toObject<Record>().copy(id = doc.id)
            val date = LocalDate.ofEpochDay(record.date)
            editRecord(
                oldRecord = record,
                newDate = date.plusDays(daysDiff),
                newName = newName,
                newPriceText = newPriceText
            )
        }

        return records.size()
    }

    override fun deleteRecord(recordId: String) {
        recordsDb.get().document(recordId).delete()
    }

    override suspend fun deleteBatch(record: Record): Int {
        // If record isn't batched for some reason, simply delete it.
        if (!record.isBatched) {
            deleteRecord(record.id)
            return 1
        }

        val records = getAllTheFutureRecords(record)
        records.forEach { snapshot ->
            deleteRecord(snapshot.id)
        }

        return records.size()
    }

    private suspend fun getAllTheFutureRecords(record: Record): QuerySnapshot {
        return recordsDb.get()
            .whereEqualTo("batchId", record.batchId)
            .whereGreaterThanOrEqualTo("date", record.date)
            .get()
            .await()
    }
}