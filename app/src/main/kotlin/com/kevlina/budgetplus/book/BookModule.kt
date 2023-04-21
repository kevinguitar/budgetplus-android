package com.kevlina.budgetplus.book

import android.app.Activity
import com.kevlina.budgetplus.core.ui.Book
import com.kevlina.budgetplus.core.ui.SnackbarSender
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import kotlin.reflect.KClass

@Module
@InstallIn(SingletonComponent::class)
interface BookModule {

    @Binds @Book
    fun provideBookSnackbarSender(impl: BookSnackbarSender): SnackbarSender

    companion object {

        @Provides @Named("book")
        fun provideBookDest(): KClass<out Activity> = BookActivity::class
    }
}