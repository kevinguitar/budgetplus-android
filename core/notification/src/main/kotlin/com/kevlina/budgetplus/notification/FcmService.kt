package com.kevlina.budgetplus.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.ARG_SHOW_MEMBERS
import com.kevlina.budgetplus.core.common.nav.AddDest
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
        Timber.d("RemoteMessage: ${message.data}")
        val channelId = when (message.data["type"]) {
            CHANNEL_NEW_MEMBER -> CHANNEL_NEW_MEMBER
            CHANNEL_GENERAL -> CHANNEL_GENERAL
            else -> CHANNEL_GENERAL
        }

        val contentIntent = Intent(Intent.ACTION_VIEW)
        contentIntent.data = if (channelId == CHANNEL_NEW_MEMBER) {
            // Open the record screen and show the members dialog
            "$APP_DEEPLINK/${AddDest.Record.route}?$ARG_SHOW_MEMBERS=true"
        } else {
            message.data["url"] ?: "$APP_DEEPLINK/${AddDest.Record.route}"
        }.toUri()

        val pendingIntent: PendingIntent? = PendingIntent.getActivity(
            /* context = */ this,
            /* requestCode = */ NOTIFICATION_REQ_CODE,
            /* intent = */ contentIntent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_logo_24dp)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        showNotification(notification)
    }

    private var notificationId: Int = 0

    @SuppressLint("MissingPermission")
    private fun showNotification(notification: Notification) {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId++, notification)
    }

    companion object {
        private const val NOTIFICATION_REQ_CODE = 5234
    }
}