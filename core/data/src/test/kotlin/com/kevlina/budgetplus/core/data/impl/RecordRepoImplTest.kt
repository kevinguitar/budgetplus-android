package com.kevlina.budgetplus.core.data.impl

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.impl.FakeToaster
import com.kevlina.budgetplus.core.common.impl.FakeTracker
import com.kevlina.budgetplus.core.common.withCurrentTime
import com.kevlina.budgetplus.core.data.BatchFrequency
import com.kevlina.budgetplus.core.data.remote.Author
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.User
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class RecordRepoImplTest {

    @Test
    fun `createRecord should store the record in DB`() = runTest {
        createRepo().createRecord(testRecord)

        verify { recordsDb.add(testRecord) }
        assertThat(tracker.lastEventName).isEqualTo("record_created")
    }

    @Test
    fun `batchRecord should record correct amount of records`() = runTest {
        val nTimes = 5
        val startDate = LocalDate.now()

        val batchId = createRepo().batchRecord(
            record = testRecord,
            startDate = startDate,
            frequency = BatchFrequency.Weekly,
            times = nTimes
        )

        repeat(nTimes) { index ->
            val batchDate = startDate.plusWeeks(index.toLong())
            verify(exactly = 1) {
                recordsDb.add(testRecord.copy(
                    date = batchDate.toEpochDay(),
                    timestamp = batchDate.withCurrentTime,
                    batchId = batchId
                ))
            }
        }
        assertThat(tracker.lastEventName).isEqualTo("record_batched")
    }

    @Test
    fun `editRecord should keep the record's original time`() = runTest {
        val localTime = LocalTime.now().minusHours(1)
        val timestamp = LocalDateTime.of(LocalDate.now(), localTime).toEpochSecond(ZoneOffset.UTC)
        val oldRecord = testRecord.copy(
            id = "old_record_id",
            timestamp = timestamp
        )
        val newDate = LocalDate.now().plusYears(1)

        createRepo().editRecord(
            oldRecord = oldRecord,
            newDate = newDate,
            newCategory = "New category",
            newName = "New name",
            newPriceText = "12345.6"
        )

        verify { recordsDb.document("old_record_id") }
        verify {
            documentReference.set(oldRecord.copy(
                date = newDate.toEpochDay(),
                timestamp = LocalDateTime.of(newDate, localTime).toEpochSecond(ZoneOffset.UTC),
                category = "New category",
                name = "New name",
                price = 12345.6
            ))
        }
        assertThat(tracker.lastEventName).isEqualTo("record_edited")
    }

    @Test
    fun `duplicateRecord should use the current user as author, and do not carry batch info`() = runTest {
        createRepo().duplicateRecord(testRecord)

        verify {
            recordsDb.add(testRecord.copy(
                author = Author(name = "My user")
            ))
        }
        assertThat(tracker.lastEventName).isEqualTo("record_duplicated")
    }

    @Test
    fun `deleteRecord should delete the record`() = runTest {
        createRepo().deleteRecord("old_record_id")

        verify { recordsDb.document("old_record_id") }
        verify { documentReference.delete() }
        assertThat(tracker.lastEventName).isEqualTo("record_deleted")
    }


    private val testRecord = Record(
        type = RecordType.Expense,
        category = "Test category",
        name = "Dinner",
        price = 124.56,
        author = Author(name = "Test user"),
        date = LocalDate.now().toEpochDay(),
        timestamp = LocalDate.now().withCurrentTime,
    )

    private val documentReference = mockk<DocumentReference>(relaxed = true)

    private val recordsDb = mockk<CollectionReference> {
        every { add(any()) } returns mockk()
        every { document(any()) } returns documentReference
    }

    private val authManger = FakeAuthManager(user = User(name = "My user"))
    private val tracker = FakeTracker()
    private val toaster = FakeToaster()

    context(TestScope)
    private fun createRepo() = RecordRepoImpl(
        recordsDb = { recordsDb },
        appScope = backgroundScope,
        authManager = authManger,
        tracker = tracker,
        toaster = toaster
    )
}