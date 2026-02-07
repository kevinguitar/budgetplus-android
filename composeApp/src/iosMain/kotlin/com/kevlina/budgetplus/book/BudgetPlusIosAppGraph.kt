package com.kevlina.budgetplus.book

import com.kevlina.budgetplus.core.common.di.ViewModelGraphProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph

@DependencyGraph(AppScope::class)
interface BudgetPlusIosAppGraph {
    val viewModelGraphProvider: ViewModelGraphProvider
}

object BudgetPlusIosAppGraphHolder {
    val graph by lazy { createGraph<BudgetPlusIosAppGraph>() }
}