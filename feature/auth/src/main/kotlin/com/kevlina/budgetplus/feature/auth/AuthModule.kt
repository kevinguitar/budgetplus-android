@file:Suppress("DEPRECATION")

package com.kevlina.budgetplus.feature.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.feature.auth.AuthActivity.Companion.ARG_ENABLE_ONE_TAP
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
    fun provideGoogleSignInOptions(stringProvider: StringProvider): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(stringProvider[R.string.google_cloud_client_id])
            .build()
    }

    @Provides
    @Named("logout")
    fun provideLogoutNavigationAction(@ApplicationContext context: Context): NavigationAction {
        return NavigationAction(
            intent = Intent(context, AuthActivity::class.java)
                .putExtra(ARG_ENABLE_ONE_TAP, false)
        )
    }
}