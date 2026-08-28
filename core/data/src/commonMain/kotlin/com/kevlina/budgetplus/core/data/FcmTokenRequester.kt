package com.kevlina.budgetplus.core.data

/**
 * Interface for requesting the FCM token.
 */
interface FcmTokenRequester {

    /**
     * Always return null on Android, token will be received in FcmService.onRegistered.
     */
    suspend fun getToken(): String?
}
