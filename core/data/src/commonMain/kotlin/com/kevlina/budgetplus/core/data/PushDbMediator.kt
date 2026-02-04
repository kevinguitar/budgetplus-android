package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.PushNotificationData
import com.kevlina.budgetplus.core.data.remote.PushNotificationsDb
import dev.gitlive.firebase.firestore.CollectionReference
import dev.zacsweers.metro.Inject

@Inject
class PushDbMediator(
    @PushNotificationsDb private val pushNotificationsDb: Lazy<CollectionReference>,
) {
    suspend fun recordPushNotification(pushNotificationData: PushNotificationData) {
        pushNotificationsDb.value
            .add(pushNotificationData)
    }
}