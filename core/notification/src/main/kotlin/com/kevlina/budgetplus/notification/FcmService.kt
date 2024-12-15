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
import com.kevlina.budgetplus.core.common.nav.NAV_SETTINGS_PATH
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.notification.channel.NotificationChannelsInitializer.Companion.CHANNEL_GENERAL
import com.kevlina.budgetplus.notification.channel.NotificationChannelsInitializer.Companion.CHANNEL_NEW_MEMBER
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject lateinit var authManager: Lazy<AuthManager>
    @Inject lateinit var imageLoader: ImageLoader
    @Inject @Named("default_deeplink") lateinit var defaultDeeplink: String
    @Inject @AppScope lateinit var appScope: CoroutineScope

    private var notificationId: Int = 0

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        authManager.get().updateFcmToken(newToken = token)
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
            // the query must match AddDest.Settings
            "$APP_DEEPLINK/$NAV_SETTINGS_PATH?showMembers=true"
        } else {
            message.data["url"] ?: defaultDeeplink
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
                    .setSmallIcon(R.drawable.ic_logo)
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

    @SuppressLint("MissingPermission")
    private fun showNotification(notification: Notification) {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId++, notification)
    }

    companion object {
        private const val NOTIFICATION_REQ_CODE = 5234
    }
}