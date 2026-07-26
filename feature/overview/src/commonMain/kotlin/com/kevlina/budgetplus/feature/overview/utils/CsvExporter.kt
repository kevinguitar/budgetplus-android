package com.kevlina.budgetplus.feature.overview.utils

import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.export_column_author
import budgetplus.core.common.generated.resources.export_column_category
import budgetplus.core.common.generated.resources.export_column_created_on
import budgetplus.core.common.generated.resources.export_column_name
import budgetplus.core.common.generated.resources.export_column_price
import budgetplus.core.common.generated.resources.export_column_type
import budgetplus.core.common.generated.resources.record_expense
import budgetplus.core.common.generated.resources.record_income
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.plainPriceString
import com.kevlina.budgetplus.core.common.shortFormatted
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.CurrencyDisplay
import com.kevlina.budgetplus.core.data.CurrencyExchangeRepo
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.UserRepo
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.createdOn
import com.kevlina.budgetplus.core.data.resolveAuthor
import de.halfbit.csv.buildCsv
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.invoke
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import kotlin.time.Instant

@Inject
internal class CsvExporter(
    private val csvSaver: CsvSaver,
    private val recordsObserver: RecordsObserver,
    private val userRepo: UserRepo,
    private val bookRepo: BookRepo,
    private val currencyExchangeRepo: CurrencyExchangeRepo,
) {
    suspend fun downloadRecordsToCsv(fileName: String) {
        val bookCurrencySymbol = bookRepo.currencySymbol.value
        val preferredCurrencySymbol = currencyExchangeRepo.preferredCurrencySymbol.first()
        val shouldExportInPreferredCurrency = preferredCurrencySymbol != null &&
            bookCurrencySymbol != preferredCurrencySymbol

        val labels = CsvLabels(
            createdOn = getString(Res.string.export_column_created_on),
            name = getString(Res.string.export_column_name),
            bookPrice = getString(Res.string.export_column_price, bookCurrencySymbol),
            preferredPrice = if (shouldExportInPreferredCurrency) {
                getString(Res.string.export_column_price, preferredCurrencySymbol)
            } else {
                null
            },
            type = getString(Res.string.export_column_type),
            category = getString(Res.string.export_column_category),
            author = getString(Res.string.export_column_author),
            expense = getString(Res.string.record_expense),
            income = getString(Res.string.record_income),
        )

        val recordRows = generateRecordRows(shouldExportInPreferredCurrency, labels)
        val csvText = buildCsvText(labels, shouldExportInPreferredCurrency, recordRows)
        csvSaver.saveToDownload(fileName, csvText)
    }

    private suspend fun generateRecordRows(
        shouldExportInPreferredCurrency: Boolean,
        labels: CsvLabels,
    ): Sequence<List<String>> = Dispatchers.Default {
        val rawRecords = recordsObserver.records.filterNotNull().first()
        rawRecords
            .sortedBy { it.createdOn }
            .map { record ->
                buildRecordRow(
                    record = record,
                    labels = labels,
                    shouldExportInPreferredCurrency = shouldExportInPreferredCurrency,
                    resolveAuthorName = { userRepo.resolveAuthor(it).author?.name.orEmpty() },
                    resolvePreferredPrice = {
                        currencyExchangeRepo.getDisplayPrice(
                            record = it,
                            display = CurrencyDisplay.Preferred
                        )
                    },
                )
            }
    }

    /**
     * Resolved, locale-specific labels used when building the CSV. Extracted so the pure CSV
     * building logic can be unit tested without resolving Compose string resources.
     */
    internal data class CsvLabels(
        val createdOn: String,
        val name: String,
        val bookPrice: String,
        val preferredPrice: String?,
        val type: String,
        val category: String,
        val author: String,
        val expense: String,
        val income: String,
    )

    internal companion object {

        fun buildRecordRow(
            record: Record,
            labels: CsvLabels,
            shouldExportInPreferredCurrency: Boolean,
            resolveAuthorName: (Record) -> String,
            resolvePreferredPrice: (Record) -> Double,
        ): List<String> = listOfNotNull(
            record.parseDatetime(),
            record.name,
            record.price.plainPriceString,
            if (shouldExportInPreferredCurrency) {
                resolvePreferredPrice(record).plainPriceString
            } else {
                null
            },
            when (record.type) {
                RecordType.Expense -> labels.expense
                RecordType.Income -> labels.income
            },
            record.category,
            resolveAuthorName(record),
        )

        fun buildCsvText(
            labels: CsvLabels,
            shouldExportInPreferredCurrency: Boolean,
            recordRows: Sequence<List<String>>,
        ): String {
            val columns = listOfNotNull(
                labels.createdOn,
                labels.name,
                labels.bookPrice,
                if (shouldExportInPreferredCurrency) labels.preferredPrice else null,
                labels.type,
                labels.category,
                labels.author,
            )
            val csv = buildCsv {
                header {
                    columns.forEach(::column)
                }
                recordRows.forEach { row ->
                    data {
                        row.forEach(::value)
                    }
                }
            }
            return csv.toCsvText()
        }

        private fun Record.parseDatetime(): String {
            return Instant.fromEpochSeconds(createdOn)
                .toLocalDateTime(TimeZone.UTC)
                .shortFormatted
        }
    }
}
