package com.kevlina.budgetplus.notification.channel

import com.kevlina.budgetplus.core.common.AppStartAction
import com.kevlina.budgetplus.notification.NotificationTopicSubscriber
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

@Module
@ContributesTo(AppScope::class)
@InstallIn(SingletonComponent::class)
interface NotificationModule {

    @Binds @IntoSet
    fun provideNotificationChannelsInitializer(
        impl: NotificationChannelsInitializer,
    ): AppStartAction

    @Binds @IntoSet
    fun provideNotificationTopicSubscriber(
        impl: NotificationTopicSubscriber,
    ): AppStartAction
}