package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.common.Logger
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.awaitResult
import dev.gitlive.firebase.messaging.ios
import dev.gitlive.firebase.messaging.messaging
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class FcmTokenRequesterImpl : FcmTokenRequester {

    override suspend fun getToken(): String? {
        val token: String? = try {
            awaitResult { Firebase.messaging.ios.tokenWithCompletion(it) }
        } catch (e: Exception) {
            Logger.e(e, "Failed to get FCM token")
            null
        }
        Logger.d("Fcm token: $token")
        return token
    }
}