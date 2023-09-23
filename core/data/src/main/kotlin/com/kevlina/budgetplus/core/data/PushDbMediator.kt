package com.kevlina.budgetplus.core.data

import com.google.firebase.firestore.CollectionReference
import com.kevlina.budgetplus.core.data.remote.PushNotificationData
import com.kevlina.budgetplus.core.data.remote.PushNotificationsDb
import dagger.Reusable
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@Reusable
class PushDbMediator @Inject constructor(
    @PushNotificationsDb private val pushNotificationsDb: dagger.Lazy<CollectionReference>,
) {

    suspend fun recordPushNotification(pushNotificationData: PushNotificationData) {
        pushNotificationsDb.get()
            .add(pushNotificationData)
            .await()
    }
}