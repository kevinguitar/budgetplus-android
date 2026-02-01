package com.kevlina.budgetplus.feature.overview

import android.os.Build

actual val shouldRequestStoragePermission: Boolean
    get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q