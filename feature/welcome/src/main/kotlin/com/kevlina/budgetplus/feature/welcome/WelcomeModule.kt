package com.kevlina.budgetplus.feature.welcome

import android.app.Activity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import kotlin.reflect.KClass

@Module
@InstallIn(SingletonComponent::class)
object WelcomeModule {

    @Provides
    @Named("welcome")
    fun provideWelcomeDest(): KClass<out Activity> = WelcomeActivity::class
}