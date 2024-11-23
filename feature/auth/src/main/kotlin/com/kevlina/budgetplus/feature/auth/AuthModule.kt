package com.kevlina.budgetplus.feature.auth

import android.content.Context
import android.content.Intent
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.feature.auth.AuthActivity.Companion.ARG_ENABLE_AUTO_SIGN_IN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Named("logout")
    fun provideLogoutNavigationAction(@ApplicationContext context: Context): NavigationAction {
        return NavigationAction(
            intent = Intent(context, AuthActivity::class.java)
                .putExtra(ARG_ENABLE_AUTO_SIGN_IN, false)
        )
    }
}