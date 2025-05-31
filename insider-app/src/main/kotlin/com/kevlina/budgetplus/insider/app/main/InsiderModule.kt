package com.kevlina.budgetplus.insider.app.main

import android.content.Context
import android.content.Intent
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object InsiderModule {

    @Provides @Named("book")
    fun provideBookNavigationAction(@ApplicationContext context: Context): NavigationAction {
        return NavigationAction(intent = Intent(context, InsiderActivity::class.java))
    }

    @Provides @Named("welcome")
    fun provideWelcomeNavigationAction(@ApplicationContext context: Context): NavigationAction {
        return NavigationAction(intent = Intent(context, InsiderActivity::class.java))
    }
}