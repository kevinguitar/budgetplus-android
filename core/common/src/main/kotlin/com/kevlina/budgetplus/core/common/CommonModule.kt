package com.kevlina.budgetplus.core.common

import android.content.Context
import com.kevlina.budgetplus.core.common.impl.ToasterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface CommonModule {

    @Binds
    fun provideToaster(impl: ToasterImpl): Toaster

    companion object {

        @Provides
        @AppScope
        @Singleton
        fun provideAppCoroutineScope(): CoroutineScope = AppCoroutineScope

        @Provides
        @Named("is_debug")
        fun provideIsDebug(@ApplicationContext context: Context): Boolean {
            return context.resources.getBoolean(R.bool.is_debug)
        }

        @Provides
        @Named("app_package")
        fun provideAppPackage(): String {
            return "com.kevlina.budgetplus"
        }
    }
}