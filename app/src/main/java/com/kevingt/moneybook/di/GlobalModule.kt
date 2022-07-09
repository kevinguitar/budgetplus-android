package com.kevingt.moneybook.di

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.auth.AuthManagerImpl
import com.kevingt.moneybook.book.overview.vm.TimePeriod
import com.kevingt.moneybook.book.overview.vm.TimePeriodTypeAdapter
import com.kevingt.moneybook.data.local.AppPreference
import com.kevingt.moneybook.data.local.Preference
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.BookRepoImpl
import com.kevingt.moneybook.data.remote.RecordRepo
import com.kevingt.moneybook.data.remote.RecordRepoImpl
import com.kevingt.moneybook.utils.AppCoroutineScope
import com.kevingt.moneybook.utils.AppScope
import com.kevingt.moneybook.utils.Toaster
import com.kevingt.moneybook.utils.ToasterImpl
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
        @Singleton
        fun provideGson(): Gson = GsonBuilder()
            .registerTypeHierarchyAdapter(TimePeriod::class.java, TimePeriodTypeAdapter())
            .create()

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