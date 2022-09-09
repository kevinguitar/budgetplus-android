package com.kevlina.budgetplus.utils

import android.os.Build
import android.os.Bundle
import java.io.Serializable

inline fun <reified T : Serializable> Bundle.getSerializableCompat(key: String): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requireNotNull(getSerializable(key, T::class.java))
    } else {
        @Suppress("DEPRECATION")
        getSerializable(key) as T
    }
}