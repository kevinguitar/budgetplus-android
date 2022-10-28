package com.kevlina.budgetplus.core.common

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import com.kevlina.budgetplus.core.common.impl.ToasterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
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
        fun provideVibrator(@ApplicationContext context: Context): Vibrator {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                    .defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        }
    }
}