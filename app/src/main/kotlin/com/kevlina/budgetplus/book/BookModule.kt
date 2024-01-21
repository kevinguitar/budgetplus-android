package com.kevlina.budgetplus.book

import android.content.Context
import android.content.Intent
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.core.ui.Book
import com.kevlina.budgetplus.core.ui.SnackbarSender
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
interface BookModule {

    @Binds @Book
    fun provideBookSnackbarSender(impl: BookSnackbarSender): SnackbarSender

    companion object {

        @Provides @Named("book")
        fun provideBookNavigationAction(@ApplicationContext context: Context): NavigationAction {
            return NavigationAction(intent = Intent(context, BookActivity::class.java))
        }
    }
}