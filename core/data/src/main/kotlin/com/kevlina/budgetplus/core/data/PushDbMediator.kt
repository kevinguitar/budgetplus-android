package com.kevlina.budgetplus.core.data

import com.google.firebase.firestore.CollectionReference
import com.kevlina.budgetplus.core.data.remote.PushNotificationData
import com.kevlina.budgetplus.core.data.remote.PushNotificationsDb
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.tasks.await

class PushDbMediator @Inject constructor(
    @PushNotificationsDb private val pushNotificationsDb: Lazy<CollectionReference>,
) {

    suspend fun recordPushNotification(pushNotificationData: PushNotificationData) {
        pushNotificationsDb.value
            .add(pushNotificationData)
            .await()
    }
}