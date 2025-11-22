package com.kevlina.budgetplus.app

import android.app.Application
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
interface BudgetPlusAppGraph {

    @Binds
    val BudgetPlusApp.bindApplication: Application

    @Binds
    @ApplicationContext
    fun BudgetPlusApp.bindContext(): Context

    @DependencyGraph.Factory
    interface Factory {
        fun create(@Provides app: BudgetPlusApp): BudgetPlusAppGraph
    }
}