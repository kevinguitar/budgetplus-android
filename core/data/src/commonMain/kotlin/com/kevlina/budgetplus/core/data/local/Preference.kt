package com.kevlina.budgetplus.core.data.local

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Preference {

    fun <T> of(key: Preferences.Key<T>): Flow<T?>

    fun <T> of(
        key: Preferences.Key<T>,
        default: T,
        scope: CoroutineScope,
    ): StateFlow<T>

    suspend fun <T> update(
        key: Preferences.Key<T>,
        value: T?,
    )

    suspend fun clearAll()

}