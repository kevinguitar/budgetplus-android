package com.kevlina.budgetplus.feature.welcome

import android.content.Context
import android.content.Intent
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import javax.inject.Named

@Module
@ContributesTo(AppScope::class)
@InstallIn(SingletonComponent::class)
interface WelcomeModule {

    companion object {
        @Provides
        @Named("welcome")
        fun provideWelcomeNavigationAction(@ApplicationContext context: Context): NavigationAction {
            return NavigationAction(intent = Intent(context, WelcomeActivity::class.java))
        }
    }
}