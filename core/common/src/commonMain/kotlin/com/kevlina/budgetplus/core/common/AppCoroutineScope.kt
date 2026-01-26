package com.kevlina.budgetplus.core.common

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Qualifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@Qualifier
annotation class AppCoroutineScope

@AppCoroutineScope
@ContributesBinding(AppScope::class)
object AppCoroutineScopeImpl : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}