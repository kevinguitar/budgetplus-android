package com.kevlina.budgetplus.feature.utils

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.dollar
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.UserRepo
import com.kevlina.budgetplus.core.data.remote.Record
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject

internal class CsvWriter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordsObserver: RecordsObserver,
    private val userRepo: UserRepo,
) {

    private val contentResolver get() = context.contentResolver

    suspend fun writeRecordsToCsv(name: String): Uri {

        val rawRecords = recordsObserver.records.filterNotNull().first()

        val datetimeFormatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.getDefault())

        val recordRows = withContext(Dispatchers.Default) {
            rawRecords
                .sortedBy { it.createdOn }
                .map { record ->
                    listOf(
                        record.parseDatetime(datetimeFormatter),
                        record.name,
                        record.price.dollar,
                        record.typeString,
                        record.category,
                        userRepo.resolveAuthor(record).author?.name
                    )
                }
        }

        return withContext(Dispatchers.IO) {
            val filename = "$name.csv"
            val fileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getFileUri(filename)
            } else {
                getFileUriPreQ(filename)
            }
            val outputStream = contentResolver.openOutputStream(fileUri)
                ?: error("Cannot open output stream from $fileUri")

            outputStream.use { stream ->
                csvWriter().open(stream) {
                    writeRow(listOf(
                        context.getString(R.string.export_column_created_on),
                        context.getString(R.string.export_column_name),
                        context.getString(R.string.export_column_price),
                        context.getString(R.string.export_column_type),
                        context.getString(R.string.export_column_category),
                        context.getString(R.string.export_column_author),
                    ))
                    writeRows(recordRows)
                }
            }
            fileUri
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun getFileUri(filename: String): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        return resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: error("Cannot resolve $contentValues")
    }

    private fun getFileUriPreQ(filename: String): Uri {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return File(dir, filename).toUri()
    }

    private val Record.typeString: String
        get() = when (type) {
            RecordType.Expense -> context.getString(R.string.record_expense)
            RecordType.Income -> context.getString(R.string.record_income)
        }

    private fun Record.parseDatetime(formatter: DateTimeFormatter): String {
        return LocalDateTime
            .ofEpochSecond(createdOn, 0, ZoneOffset.UTC)
            .format(formatter)
    }
}