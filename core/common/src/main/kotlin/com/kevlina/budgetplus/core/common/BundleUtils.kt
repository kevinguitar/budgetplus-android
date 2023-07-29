package com.kevlina.budgetplus.core.common

import android.os.Build
import android.os.Bundle
import java.io.Serializable

inline fun bundle(init: Bundle.() -> Unit) = Bundle().apply(init)

inline fun <reified T : Serializable> Bundle.getSerializableCompat(key: String): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requireNotNull(getSerializable(key, T::class.java))
    } else {
        @Suppress("DEPRECATION")
        getSerializable(key) as T
    }
}