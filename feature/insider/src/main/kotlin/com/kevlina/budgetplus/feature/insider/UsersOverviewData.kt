package com.kevlina.budgetplus.feature.insider

internal data class InsiderData(
    val totalPremiumUsers: Long,
    val dailyActiveUsers: Long,
)

internal data class UsersOverviewData(
    val totalUsers: Long,
    val totalEnglishUsers: Long,
    val totalJapaneseUsers: Long,
    val totalSimplifiedChineseUsers: Long,
)
