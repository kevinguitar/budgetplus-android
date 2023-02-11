package com.kevlina.budgetplus.notification

import android.annotation.SuppressLint
import android.app.Notification
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.notification.channel.CHANNEL_GENERAL
import com.kevlina.budgetplus.notification.channel.CHANNEL_NEW_MEMBER
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject lateinit var authManager: AuthManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        authManager.updateFcmToken(newToken = token)
        Timber.d("New fcm token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val channelId = when (message.data["type"]) {
            CHANNEL_NEW_MEMBER -> CHANNEL_NEW_MEMBER
            CHANNEL_GENERAL -> CHANNEL_GENERAL
            else -> CHANNEL_GENERAL
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .build()

        showNotification(notification)
    }

    private var notificationId: Int = 0

    @SuppressLint("MissingPermission")
    private fun showNotification(notification: Notification) {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId++, notification)
    }
}