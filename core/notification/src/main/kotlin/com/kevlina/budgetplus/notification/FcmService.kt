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
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.ImageLoader
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.ARG_SHOW_MEMBERS
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.notification.channel.NotificationChannelsInitializer.Companion.CHANNEL_GENERAL
import com.kevlina.budgetplus.notification.channel.NotificationChannelsInitializer.Companion.CHANNEL_NEW_MEMBER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject lateinit var authManager: AuthManager
    @Inject lateinit var imageLoader: ImageLoader
    @Inject @AppScope lateinit var appScope: CoroutineScope

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        authManager.updateFcmToken(newToken = token)
        Timber.d("New fcm token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Timber.d("RemoteMessage: ${message.data}")
        val recipientId = message.data["recipientId"]
        if (recipientId != null && recipientId != authManager.userState.value?.id) {
            // Do not show it if the recipient isn't targeting the current logged-in user.
            return
        }

        val channelId = when (message.data["type"]) {
            CHANNEL_NEW_MEMBER -> CHANNEL_NEW_MEMBER
            CHANNEL_GENERAL -> CHANNEL_GENERAL
            else -> CHANNEL_GENERAL
        }

        val contentIntent = Intent(Intent.ACTION_VIEW)
        contentIntent.data = if (channelId == CHANNEL_NEW_MEMBER) {
            // Open the record screen and show the members dialog
            "$APP_DEEPLINK/${AddDest.Settings.route}?$ARG_SHOW_MEMBERS=true"
        } else {
            message.data["url"] ?: "$APP_DEEPLINK/${AddDest.Record.route}"
        }.toUri()

        val pendingIntent: PendingIntent? = PendingIntent.getActivity(
            /* context = */ this,
            /* requestCode = */ NOTIFICATION_REQ_CODE,
            /* intent = */ contentIntent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val smallImageUrl = message.data["smallImageUrl"]
        val largeImageUrl = message.data["largeImageUrl"]

        appScope.launch(Dispatchers.IO) {
            val smallImage = imageLoader.loadBitmap(smallImageUrl)
            val largeImage = imageLoader.loadBitmap(largeImageUrl)

            withContext(Dispatchers.Main) {
                val bigPictureStyle = if (largeImage != null) {
                    NotificationCompat.BigPictureStyle().bigPicture(largeImage)
                } else {
                    null
                }

                val notification = NotificationCompat.Builder(this@FcmService, channelId)
                    .setSmallIcon(R.drawable.ic_logo_24dp)
                    .setContentTitle(message.data["title"])
                    .setContentText(message.data["body"])
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(smallImage)
                    .setStyle(bigPictureStyle)
                    .setAutoCancel(true)
                    .build()

                showNotification(notification)
            }
        }
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