package com.kevlina.budgetplus.notification.channel

import com.kevlina.budgetplus.core.common.AppStartAction
import com.kevlina.budgetplus.notification.NotificationTopicSubscriber
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal interface NotificationModule {

    @Binds @IntoSet
    fun provideNotificationChannelsInitializer(
        impl: NotificationChannelsInitializer,
    ): AppStartAction

    @Binds @IntoSet
    fun provideNotificationTopicSubscriber(
        impl: NotificationTopicSubscriber,
    ): AppStartAction
}