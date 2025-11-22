package com.kevlina.budgetplus.feature.welcome

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.MembersInjector
import dev.zacsweers.metro.Provides

@GraphExtension
interface WelcomeActivityGraph {

    val injector: MembersInjector<WelcomeActivity>

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun create(@Provides activity: WelcomeActivity): WelcomeActivityGraph
    }
}