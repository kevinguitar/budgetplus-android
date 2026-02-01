package com.kevlina.budgetplus.feature.overview

/**
 * On iOS, exporting a CSV file via standard mechanisms (like the Share Sheet) does not require
 * broad storage permissions. The app can always write to its own container, and the system handles
 * permissions when sharing files with other applications or saving them to the "Files" app.
 */
actual val shouldRequestStoragePermission: Boolean = false
