package com.kevlina.budgetplus.insider.app.main

import kotlinx.serialization.Serializable

@Serializable
sealed interface InsiderDest {

    @Serializable
    data object Insider : InsiderDest

    @Serializable
    data object PushNotifications : InsiderDest
}