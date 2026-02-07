package com.kevlina.budgetplus.book

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kevlina.budgetplus.core.common.di.ViewModelGraphProvider
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.theme.ThemeManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraph

@DependencyGraph(AppScope::class)
interface BudgetPlusIosAppGraph {
    val viewModelGraphProvider: ViewModelGraphProvider
    val themeManager: ThemeManager
    val authManager: AuthManager
    val bookRepo: BookRepo

    val destinationState: MutableState<Destination>
    val navigation: NavigationFlow

    @SingleIn(AppScope::class)
    @Provides
    fun provideDestinationState(): MutableState<Destination> = mutableStateOf(Destination.Book)
}

object BudgetPlusIosAppGraphHolder {
    val graph by lazy { createGraph<BudgetPlusIosAppGraph>() }
}