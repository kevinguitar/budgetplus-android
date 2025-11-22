package com.kevlina.budgetplus.feature.welcome

import android.content.Context
import android.content.Intent
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface WelcomeModule {

    @Provides
    @Named("welcome")
    fun provideWelcomeNavigationAction(context: Context): NavigationAction {
        return NavigationAction(intent = Intent(context, WelcomeActivity::class.java))
    }
}