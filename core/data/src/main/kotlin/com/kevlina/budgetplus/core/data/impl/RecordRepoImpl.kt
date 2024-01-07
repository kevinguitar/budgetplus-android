package com.kevlina.budgetplus.core.data.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.bundle
import com.kevlina.budgetplus.core.common.parseToPrice
import com.kevlina.budgetplus.core.common.withCurrentTime
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BatchFrequency
import com.kevlina.budgetplus.core.data.CategoryRenameEvent
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.RecordsDb
import com.kevlina.budgetplus.core.data.remote.toAuthor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
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
    @AppScope private val appScope: CoroutineScope,
    private val authManager: AuthManager,
    private val tracker: Tracker,
    private val toaster: Toaster,
) : RecordRepo {

    override fun createRecord(record: Record) {
        recordsDb.get().add(record)
        tracker.logEvent("record_created")
    }

    override fun batchRecord(
        record: Record,
        startDate: LocalDate,
        frequency: BatchFrequency,
        times: Int,
    ) {
        val batchId = UUID.randomUUID().toString()
        var currentDate: LocalDate

        repeat(times) { index ->
            currentDate = when (frequency) {
                BatchFrequency.Monthly -> startDate.plusMonths(index.toLong())
                BatchFrequency.Weekly -> startDate.plusWeeks(index.toLong())
                BatchFrequency.Daily -> startDate.plusDays(index.toLong())
            }

            createRecord(record.copy(
                date = currentDate.toEpochDay(),
                timestamp = currentDate.withCurrentTime,
                batchId = batchId
            ))
        }
        tracker.logEvent("record_batched")
    }

    override fun editRecord(
        oldRecord: Record,
        newDate: LocalDate,
        newCategory: String,
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
                category = newCategory,
                name = newName,
                price = newPriceText.parseToPrice
            )
        } catch (e: Exception) {
            toaster.showError(e)
            return
        }

        recordsDb.get().document(oldRecord.id).set(newRecord)
        tracker.logEvent("record_edited")
    }

    override suspend fun editBatch(
        oldRecord: Record,
        newDate: LocalDate,
        newCategory: String,
        newName: String,
        newPriceText: String,
    ): Int {
        // If record isn't batched for some reason, simply edit it.
        if (!oldRecord.isBatched) {
            editRecord(
                oldRecord = oldRecord,
                newDate = newDate,
                newCategory = newCategory,
                newName = newName,
                newPriceText = newPriceText
            )
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
                newCategory = newCategory,
                newName = newName,
                newPriceText = newPriceText
            )
        }

        tracker.logEvent("record_batch_edited")
        return records.size()
    }

    override fun duplicateRecord(record: Record) {
        val duplicatedRecord = record.copy(
            id = "",
            // The person who duplicate it should be the author
            author = authManager.userState.value?.toAuthor(),
            // Do not carry the batch info to duplicates
            batchId = null
        )
        recordsDb.get().add(duplicatedRecord)
        tracker.logEvent("record_duplicated")
    }

    override fun deleteRecord(recordId: String) {
        recordsDb.get().document(recordId).delete()
        tracker.logEvent("record_deleted")
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

        tracker.logEvent("record_batch_deleted")
        return records.size()
    }

    private suspend fun getAllTheFutureRecords(record: Record): QuerySnapshot {
        return recordsDb.get()
            .whereEqualTo("batchId", record.batchId)
            .whereGreaterThanOrEqualTo("date", record.date)
            .get()
            .await()
    }

    override fun renameCategories(
        events: List<CategoryRenameEvent>,
    ) = appScope.launch(Dispatchers.IO) {
        val db = recordsDb.get()
        var dbUpdateCount = 0

        events.forEach { event ->
            try {
                val records = db
                    .whereEqualTo("category", event.from)
                    .get()
                    .await()

                records.forEach { doc ->
                    val newRecord = doc.toObject<Record>().copy(category = event.to)
                    db.document(doc.id).set(newRecord)
                }
                dbUpdateCount += records.size()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        if (dbUpdateCount > 0) {
            tracker.logEvent(
                event = "categories_renamed",
                params = bundle { putInt("db_update_count", dbUpdateCount) }
            )
        }
    }
}