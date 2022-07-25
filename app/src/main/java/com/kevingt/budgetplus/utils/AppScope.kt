package com.kevingt.budgetplus.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import kotlin.coroutines.CoroutineContext

@Qualifier
annotation class AppScope

object AppCoroutineScope : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}