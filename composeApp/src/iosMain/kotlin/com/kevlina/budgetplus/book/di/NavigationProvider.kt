package com.kevlina.budgetplus.book.di

import androidx.compose.runtime.MutableState
import com.kevlina.budgetplus.book.Destination
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface NavigationProvider {

    @Provides
    @Named("welcome")
    fun provideWelcomeNavigation(destination: MutableState<Destination>): NavigationAction =
        NavigationAction { destination.value = Destination.Welcome }

    @Provides
    @Named("book")
    fun provideBookNavigation(destination: MutableState<Destination>): NavigationAction =
        NavigationAction { destination.value = Destination.Book }

    @Provides
    @Named("auth")
    fun provideAuthNavigation(destination: MutableState<Destination>): NavigationAction =
        NavigationAction { destination.value = Destination.Auth }
}