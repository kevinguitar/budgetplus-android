package com.kevlina.budgetplus.feature.overview

import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.shortFormatted
import com.kevlina.budgetplus.core.data.remote.Author
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.createdOn
import com.kevlina.budgetplus.core.unit.test.BaseTest
import com.kevlina.budgetplus.feature.overview.utils.CsvExporter
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class CsvExporterTest : BaseTest() {

    private val labels = CsvExporter.CsvLabels(
        createdOn = "Created On",
        name = "Name",
        bookPrice = "Price ($)",
        preferredPrice = "Price (¥)",
        type = "Type",
        category = "Category",
        author = "Author",
        expense = "Expense",
        income = "Income",
    )

    private fun Long.formattedDate(): String =
        Instant.fromEpochSeconds(this)
            .toLocalDateTime(TimeZone.UTC)
            .shortFormatted

    private fun buildRow(
        record: Record,
        shouldExportInPreferredCurrency: Boolean = false,
        authorName: String = record.author?.name.orEmpty(),
        preferredPrice: Double = 0.0,
    ) = CsvExporter.buildRecordRow(
        record = record,
        labels = labels,
        shouldExportInPreferredCurrency = shouldExportInPreferredCurrency,
        resolveAuthorName = { authorName },
        resolvePreferredPrice = { preferredPrice },
    )

    @Test
    fun `WHEN building a row THEN maps the record fields in column order`() {
        val record = Record(
            type = RecordType.Expense,
            category = "Food",
            name = "Lunch",
            price = 120.0,
            author = Author(id = "u1", name = "Alice"),
            timestamp = 1_700_000_000L,
        )

        val row = buildRow(record)

        assertEquals(
            listOf(
                record.createdOn.formattedDate(),
                "Lunch",
                "120",
                "Expense",
                "Food",
                "Alice",
            ),
            row,
        )
    }

    @Test
    fun `WHEN record is an income THEN uses the income label`() {
        val record = Record(type = RecordType.Income, name = "Salary", price = 5000.0)
        assertEquals("Income", buildRow(record)[3])
    }

    @Test
    fun `WHEN record is an expense THEN uses the expense label`() {
        val record = Record(type = RecordType.Expense, name = "Rent", price = 900.0)
        assertEquals("Expense", buildRow(record)[3])
    }

    @Test
    fun `WHEN price has decimals THEN formats it as a plain price`() {
        val record = Record(name = "Snack", price = 12.356)
        assertEquals("12.36", buildRow(record)[2])
    }

    @Test
    fun `WHEN author cannot be resolved THEN the author column is empty`() {
        val record = Record(name = "Coffee", price = 5.0, author = null)
        assertEquals("", buildRow(record, authorName = "").last())
    }

    @Test
    fun `WHEN not exporting preferred currency THEN the row has six columns`() {
        val record = Record(name = "Lunch", price = 120.0)
        assertEquals(6, buildRow(record, shouldExportInPreferredCurrency = false).size)
    }

    @Test
    fun `WHEN exporting preferred currency THEN inserts the preferred price as the fourth column`() {
        val record = Record(name = "Lunch", price = 120.0)

        val row = buildRow(
            record = record,
            shouldExportInPreferredCurrency = true,
            preferredPrice = 800.0,
        )

        assertEquals(7, row.size)
        assertEquals("120", row[2])
        assertEquals("800", row[3])
    }

    @Test
    fun `WHEN building csv text THEN the header lists all columns in order`() {
        val csv = CsvExporter.buildCsvText(
            labels = labels,
            shouldExportInPreferredCurrency = false,
            recordRows = emptySequence(),
        )

        assertEquals(
            "Created On,Name,Price ($),Type,Category,Author",
            csv.trim(),
        )
    }

    @Test
    fun `WHEN exporting preferred currency THEN the header includes the preferred price column`() {
        val csv = CsvExporter.buildCsvText(
            labels = labels,
            shouldExportInPreferredCurrency = true,
            recordRows = emptySequence(),
        )

        assertEquals(
            "Created On,Name,Price ($),Price (¥),Type,Category,Author",
            csv.trim(),
        )
    }

    @Test
    fun `WHEN building csv text with rows THEN appends each row after the header`() {
        val row = buildRow(
            Record(
                name = "Lunch",
                category = "Food",
                price = 120.0,
                author = Author(id = "u1", name = "Alice"),
                timestamp = 1_700_000_000L,
            ),
        )

        val csv = CsvExporter.buildCsvText(
            labels = labels,
            shouldExportInPreferredCurrency = false,
            recordRows = sequenceOf(row),
        )

        val lines = csv.trim().lines()
        assertEquals(2, lines.size)
        // Every value of the row must be present in the serialized data line.
        row.forEach { value ->
            assertTrue(value in lines[1], "Expected '$value' in '${lines[1]}'")
        }
    }
}
