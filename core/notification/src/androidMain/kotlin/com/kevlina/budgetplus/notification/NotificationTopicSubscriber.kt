package com.kevlina.budgetplus.notification

import co.touchlab.kermit.Logger
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.kevlina.budgetplus.core.common.AppCoroutineScope
import com.kevlina.budgetplus.core.common.AppStartAction
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable

@ContributesIntoSet(AppScope::class)
class NotificationTopicSubscriber(
    @AppCoroutineScope private val appScope: CoroutineScope,
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
            Logger.d { "Notification: Subscribed to $generalTopic topic" }
            lastInfo = SubscribeInfo(
                userId = user.id,
                topic = generalTopic
            )
        } catch (e: Exception) {
            Logger.e(e) { "Notification topic subscription failed" }
        }
    }
}

@Serializable
data class SubscribeInfo(
    val userId: String,
    val topic: String,
)