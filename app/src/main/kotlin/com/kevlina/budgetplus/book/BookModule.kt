package com.kevlina.budgetplus.book

import android.app.Activity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import kotlin.reflect.KClass

@Module
@InstallIn(SingletonComponent::class)
object BookModule {

    @Provides
    @Named("book")
    fun provideBookDest(): KClass<out Activity> = BookActivity::class
}