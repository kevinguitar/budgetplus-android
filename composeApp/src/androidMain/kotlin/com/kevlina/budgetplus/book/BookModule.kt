package com.kevlina.budgetplus.book

import android.content.Context
import android.content.Intent
import com.kevlina.budgetplus.core.common.nav.AndroidNavigationAction
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface BookModule {

    @Provides @Named("book")
    fun provideBookNavigationAction(context: Context): NavigationAction {
        return AndroidNavigationAction(intent = Intent(context, BookActivity::class.java))
    }

    @Provides
    @Named("allow_update_fcm_token")
    fun provideAllowUpdateFcmToken(): Boolean = true
}