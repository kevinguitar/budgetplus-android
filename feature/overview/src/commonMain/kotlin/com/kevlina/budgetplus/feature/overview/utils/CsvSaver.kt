package com.kevlina.budgetplus.feature.overview.utils

interface CsvSaver {
    /**
     * @return The Uri of the saved file.
     */
    suspend fun saveToDownload(
        fileName: String,
        columns: List<String>,
        recordRows: Sequence<List<String?>>,
    )

    companion object {
        val MimeType get() = "text/csv"
    }
}