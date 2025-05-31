package com.kevlina.budgetplus.core.common.nav

import com.kevlina.budgetplus.core.common.RecordType
import kotlinx.serialization.Serializable

sealed interface BookTab {

    @Serializable
    data object Add : BookTab

    @Serializable
    data object History : BookTab
}

sealed interface AddDest {

    @Serializable
    data object Record : AddDest

    @Serializable
    data class EditCategory(val type: RecordType) : AddDest

    @Serializable
    data class Settings(val showMembers: Boolean = false) : AddDest

    @Serializable
    data object UnlockPremium : AddDest

    @Serializable
    data object BatchRecord : AddDest

    @Serializable
    data class Colors(val hex: String? = null) : AddDest

    @Serializable
    data object CurrencyPicker : AddDest
}

sealed interface HistoryDest {

    @Serializable
    data object Overview : HistoryDest

    @Serializable
    data class Records(
        val type: RecordType,
        val category: String,
        val authorId: String?,
    ) : HistoryDest

    @Serializable
    data class Search(val type: RecordType) : HistoryDest
}

sealed interface InsiderDest {

    @Serializable
    data object Insider : InsiderDest

    @Serializable
    data object PushNotifications : InsiderDest
}