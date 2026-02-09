package com.kevlina.budgetplus.book.di

import com.kevlina.budgetplus.core.common.nav.BookDest
import com.kevlina.budgetplus.core.common.nav.NavController
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface NavigationProvider {

    @Provides
    @Named("welcome")
    fun provideWelcomeNavigation(navController: NavController<BookDest>): NavigationAction =
        NavigationAction { navController.selectRootAndClearAll(BookDest.Welcome) }

    @Provides
    @Named("book")
    fun provideBookNavigation(navController: NavController<BookDest>): NavigationAction =
        NavigationAction { navController.selectRootAndClearAll(BookDest.Record) }

    @Provides
    @Named("auth")
    fun provideAuthNavigation(navController: NavController<BookDest>): NavigationAction =
        NavigationAction { navController.selectRootAndClearAll(BookDest.Auth) }
}