package com.kevlina.budgetplus.book.di

import co.touchlab.kermit.Logger
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface NavigationProvider {

    @Provides
    @Named("welcome")
    fun provideWelcomeNavigation(): NavigationAction = NavigationAction {
        Logger.d { "Navigating to Welcome screen" }
    }

    @Provides
    @Named("book")
    fun provideBookNavigation(): NavigationAction = NavigationAction {
        Logger.d { "Navigating to Book screen" }
    }

    @Provides
    @Named("auth")
    fun provideAuthNavigation(): NavigationAction = NavigationAction {
        Logger.d { "Navigating to Auth screen" }
    }
}