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

        val recordRows = generateRecordRows(shouldExportInPreferredCurrency)
        val columns = listOfNotNull(
            getString(Res.string.export_column_created_on),
            getString(Res.string.export_column_name),
            getString(Res.string.export_column_price, bookCurrencySymbol),
            if (shouldExportInPreferredCurrency) {
                getString(Res.string.export_column_price, preferredCurrencySymbol)
            } else {
                null
            },
            getString(Res.string.export_column_type),
            getString(Res.string.export_column_category),
            getString(Res.string.export_column_author),
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
        csvSaver.saveToDownload(fileName, csv.toCsvText())
    }

    private suspend fun generateRecordRows(
        shouldExportInPreferredCurrency: Boolean,
    ): Sequence<List<String>> = Dispatchers.Default {
        val rawRecords = recordsObserver.records.filterNotNull().first()

        val recordExpenseString = getString(Res.string.record_expense)
        val recordIncomeString = getString(Res.string.record_income)

        rawRecords
            .sortedBy { it.createdOn }
            .map { record ->
                listOfNotNull(
                    record.parseDatetime(),
                    record.name,
                    record.price.plainPriceString,
                    if (shouldExportInPreferredCurrency) {
                        currencyExchangeRepo.getDisplayPrice(
                            record = record,
                            display = CurrencyDisplay.Preferred
                        ).plainPriceString
                    } else {
                        null
                    },
                    when (record.type) {
                        RecordType.Expense -> recordExpenseString
                        RecordType.Income -> recordIncomeString
                    },
                    record.category,
                    userRepo.resolveAuthor(record).author?.name.orEmpty()
                )
            }
    }

    private fun Record.parseDatetime(): String {
        return Instant.fromEpochSeconds(createdOn)
            .toLocalDateTime(TimeZone.UTC)
            .shortFormatted
    }
}
