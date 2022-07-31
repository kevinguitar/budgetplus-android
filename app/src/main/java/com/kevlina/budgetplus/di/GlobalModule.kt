package com.kevlina.budgetplus.di

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import com.kevlina.budgetplus.auth.AuthManager
import com.kevlina.budgetplus.auth.AuthManagerImpl
import com.kevlina.budgetplus.data.local.AppPreference
import com.kevlina.budgetplus.data.local.Preference
import com.kevlina.budgetplus.data.remote.BookRepo
import com.kevlina.budgetplus.data.remote.BookRepoImpl
import com.kevlina.budgetplus.data.remote.RecordRepo
import com.kevlina.budgetplus.data.remote.RecordRepoImpl
import com.kevlina.budgetplus.utils.AppCoroutineScope
import com.kevlina.budgetplus.utils.AppScope
import com.kevlina.budgetplus.utils.Toaster
import com.kevlina.budgetplus.utils.ToasterImpl
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
interface GlobalModule {

    @Binds
    fun provideToaster(impl: ToasterImpl): Toaster

    @Binds
    fun provideAuthManager(impl: AuthManagerImpl): AuthManager

    @Binds
    fun providePreference(impl: AppPreference): Preference

    @Binds
    fun provideBookRepo(impl: BookRepoImpl): BookRepo

    @Binds
    fun provideRecordRepo(impl: RecordRepoImpl): RecordRepo

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