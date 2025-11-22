package com.kevlina.budgetplus.insider.app.main

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension
interface InsiderActivityGraph {

    fun inject(target: InsiderActivity)

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun create(@Provides activity: InsiderActivity): InsiderActivityGraph
    }
}