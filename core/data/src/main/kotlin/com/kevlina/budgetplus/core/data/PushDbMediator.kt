package com.kevlina.budgetplus.core.data

import com.google.firebase.firestore.CollectionReference
import com.kevlina.budgetplus.core.data.remote.PushNotificationData
import com.kevlina.budgetplus.core.data.remote.PushNotificationsDb
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PushDbMediator @Inject constructor(
    @PushNotificationsDb private val pushNotificationsDb: dagger.Lazy<CollectionReference>,
) {

    suspend fun recordPushNotification(pushNotificationData: PushNotificationData) {
        pushNotificationsDb.get()
            .add(pushNotificationData)
            .await()
    }
}