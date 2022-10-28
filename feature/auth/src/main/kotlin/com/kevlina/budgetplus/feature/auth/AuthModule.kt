package com.kevlina.budgetplus.feature.auth

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kevlina.budgetplus.core.common.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import kotlin.reflect.KClass

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    fun provideGoogleSignInOptions(@ApplicationContext context: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(context.getString(R.string.google_cloud_client_id))
            .build()
    }

    @Provides
    @Named("auth")
    fun provideAuthDest(): KClass<out Activity> = AuthActivity::class
}