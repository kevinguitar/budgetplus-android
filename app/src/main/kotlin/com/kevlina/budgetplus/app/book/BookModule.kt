package com.kevlina.budgetplus.app.book

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
object BookModule {

    @Provides @Named("book")
    fun provideBookNavigationAction(@ApplicationContext context: Context): NavigationAction {
        return NavigationAction(intent = Intent(context, BookActivity::class.java))
    }

    @Provides
    @Named("allow_update_fcm_token")
    fun provideAllowUpdateFcmToken(): Boolean = true
}