package com.kevlina.budgetplus.notification

import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.AppStartAction
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable
import timber.log.Timber
import javax.inject.Inject

internal class NotificationTopicSubscriber @Inject constructor(
    @AppScope private val appScope: CoroutineScope,
    private val authManager: AuthManager,
    preferenceHolder: PreferenceHolder,
) : AppStartAction {

    private var lastInfo by preferenceHolder.bindObjectOptional<SubscribeInfo?>(null)

    override fun onAppStart() {
        appScope.launch {
            subscribeToTopics()
        }
    }

    private suspend fun subscribeToTopics() {
        val user = authManager.userState
            .filterNotNull()
            .first()

        val generalTopic = when (user.language) {
            "zh-tw" -> "general_tw"
            "zh-cn" -> "general_cn"
            "ja" -> "general_ja"
            else -> "general_en"
        }
        if (lastInfo == SubscribeInfo(userId = user.id, topic = generalTopic)) {
            // Avoid resubscribing the same topic
            return
        }

        try {
            lastInfo?.let { info ->
                if (info.userId == user.id) {
                    Firebase.messaging.unsubscribeFromTopic(info.topic).await()
                }
            }

            Firebase.messaging.subscribeToTopic(generalTopic).await()
            Timber.d("Notification: Subscribed to $generalTopic topic")
            lastInfo = SubscribeInfo(
                userId = user.id,
                topic = generalTopic
            )
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}

@Serializable
data class SubscribeInfo(
    val userId: String,
    val topic: String,
)