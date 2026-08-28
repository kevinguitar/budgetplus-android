package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.common.Logger
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.android
import dev.gitlive.firebase.messaging.messaging
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.tasks.await

@ContributesBinding(AppScope::class)
internal class FcmTokenRequesterImpl : FcmTokenRequester {

    override suspend fun getToken(): String? {
        try {
            // The token will be received in FcmService.onRegistered, return null here.
            Firebase.messaging.android.register().await()
        } catch (e: Exception) {
            Logger.e(e, "Failed to retrieve the fcm token")
        }
        return null
    }
}