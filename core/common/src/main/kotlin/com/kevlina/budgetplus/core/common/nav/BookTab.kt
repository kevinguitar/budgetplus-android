package com.kevlina.budgetplus.core.common.nav

enum class BookTab {
    Add, History
}

enum class AddDest {
    Record, EditCategory, Settings, UnlockPremium,
    BatchRecord, Colors, CurrencyPicker,

    // Internal screens
    Insider, PushNotifications
}

enum class HistoryDest {
    Overview, Records
}

val <T : Enum<T>> Enum<T>.navRoute
    get() = name.replaceFirstChar { it.lowercaseChar() }