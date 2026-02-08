package com.kevlina.budgetplus.feature.overview.utils

interface CsvSaver {

    suspend fun saveToDownload(
        fileName: String,
        csvText: String,
    )

    companion object {
        val MimeType get() = "text/csv"
    }
}