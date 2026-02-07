package com.kevlina.budgetplus.feature.settings

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

actual val canSelectLanguage: Boolean
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU