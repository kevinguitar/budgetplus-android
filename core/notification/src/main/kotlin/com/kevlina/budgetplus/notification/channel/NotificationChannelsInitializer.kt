package com.kevlina.budgetplus.notification.channel

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.StringRes
import com.kevlina.budgetplus.core.common.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationChannelsInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    init {
        initNotificationChannels()
    }

    private fun initNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val generalChannel = createChannel(
            channelId = CHANNEL_GENERAL,
            nameRes = R.string.notification_channel_general,
            descriptionRes = R.string.notification_channel_general_desc
        )

        val newMemberChannel = createChannel(
            channelId = CHANNEL_NEW_MEMBER,
            nameRes = R.string.notification_channel_new_member,
            descriptionRes = R.string.notification_channel_new_member_desc
        )

        notificationManager.createNotificationChannel(generalChannel)
        notificationManager.createNotificationChannel(newMemberChannel)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel(
        channelId: String,
        @StringRes nameRes: Int,
        @StringRes descriptionRes: Int,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
    ): NotificationChannel {
        val name = context.getString(nameRes)
        val descriptionText = context.getString(descriptionRes)

        return NotificationChannel(channelId, name, importance)
            .apply { description = descriptionText }
    }
}