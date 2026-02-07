package com.kevlina.budgetplus.feature.auth

import android.content.Context
import android.content.Intent
import com.kevlina.budgetplus.core.common.nav.AndroidNavigationAction
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.feature.auth.AuthActivity.Companion.ARG_ENABLE_AUTO_SIGN_IN
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface AuthModule {

    @Provides
    @Named("auth")
    fun provideAuthNavigationAction(context: Context): NavigationAction {
        return AndroidNavigationAction(
            intent = Intent(context, AuthActivity::class.java)
                .putExtra(ARG_ENABLE_AUTO_SIGN_IN, false)
        )
    }
}