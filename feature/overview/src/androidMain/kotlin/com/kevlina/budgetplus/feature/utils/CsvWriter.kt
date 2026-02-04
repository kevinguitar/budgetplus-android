package com.kevlina.budgetplus.feature.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.export_column_author
import budgetplus.core.common.generated.resources.export_column_category
import budgetplus.core.common.generated.resources.export_column_created_on
import budgetplus.core.common.generated.resources.export_column_name
import budgetplus.core.common.generated.resources.export_column_price
import budgetplus.core.common.generated.resources.export_column_type
import budgetplus.core.common.generated.resources.record_expense
import budgetplus.core.common.generated.resources.record_income
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.plainPriceString
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.UserRepo
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.createdOn
import com.kevlina.budgetplus.core.data.resolveAuthor
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provider
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.invoke
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import java.io.File
import java.io.IOException
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.time.Instant

@Inject
internal class CsvWriter(
    private val context: Context,
    @Named("app_package") private val appPackage: String,
    @Named("share_cache") private val shareCacheDir: Provider<File>,
    private val recordsObserver: RecordsObserver,
    private val userRepo: UserRepo,
    private val bookRepo: BookRepo,
) {

    val mimeType get() = "text/csv"

    private val contentResolver get() = context.contentResolver

    suspend fun writeRecordsToCsv(name: String): Uri {
        val recordRows = generateRecordRows()
        return writeToDownload(name, recordRows)
    }

    private suspend fun generateRecordRows(): Sequence<List<String?>> = Default {
        val rawRecords = recordsObserver.records.filterNotNull().first()

        val datetimeFormatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.getDefault())

        val recordExpenseString = getString(Res.string.record_expense)
        val recordIncomeString = getString(Res.string.record_income)

        rawRecords
            .sortedBy { it.createdOn }
            .map { record ->
                listOf(
                    record.parseDatetime(datetimeFormatter),
                    record.name,
                    record.price.plainPriceString,
                    when (record.type) {
                        RecordType.Expense -> recordExpenseString
                        RecordType.Income -> recordIncomeString
                    },
                    record.category,
                    userRepo.resolveAuthor(record).author?.name
                )
            }
    }

    private suspend fun writeToDownload(
        name: String,
        recordRows: Sequence<List<String?>>,
    ): Uri = IO {
        val cacheFile = File(shareCacheDir(), "$name.csv")
        val outputStream = contentResolver.openOutputStream(cacheFile.toUri())
            ?: throw IOException("Cannot open output stream from $cacheFile")

        val columns = listOf(
            getString(Res.string.export_column_created_on),
            getString(Res.string.export_column_name),
            getString(Res.string.export_column_price, bookRepo.currencySymbol.value),
            getString(Res.string.export_column_type),
            getString(Res.string.export_column_category),
            getString(Res.string.export_column_author),
        )

        outputStream.use { stream ->
            csvWriter().open(stream) {
                writeRow(columns)
                writeRows(recordRows)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToDownload(cacheFile)
        } else {
            saveToDownloadPreQ(cacheFile)
        }

        FileProvider.getUriForFile(context, "$appPackage.provider", cacheFile)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToDownload(cacheFile: File): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, cacheFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val mediaUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw IOException("Cannot resolve ContentValues. $contentValues")

        cacheFile.inputStream().use { input ->
            resolver.openOutputStream(mediaUri)
                ?.use { output -> input.copyTo(output) }
                ?: throw IOException("Cannot open output stream from $mediaUri")
        }

        return mediaUri
    }

    private fun saveToDownloadPreQ(cacheFile: File): Uri {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val destFile = File(dir, cacheFile.name)

        cacheFile.copyTo(target = destFile, overwrite = true)
        return destFile.toUri()
    }

    private fun Record.parseDatetime(formatter: DateTimeFormatter): String {
        return Instant.fromEpochSeconds(createdOn)
            .toLocalDateTime(TimeZone.UTC)
            .toJavaLocalDateTime()
            .format(formatter)
    }
}