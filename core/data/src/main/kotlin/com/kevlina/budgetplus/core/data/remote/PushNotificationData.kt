package com.kevlina.budgetplus.core.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PushNotificationData(
    val internal: Boolean? = null,
    val titleTw: String? = null,
    val descTw: String? = null,
    val titleCn: String? = null,
    val descCn: String? = null,
    val titleJa: String? = null,
    val descJa: String? = null,
    val titleEn: String? = null,
    val descEn: String? = null,
    val deeplink: String? = null,
    val sentOn: Long? = null,
)
