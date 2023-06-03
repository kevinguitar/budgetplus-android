package com.kevlina.budgetplus.feature.insider

import com.kevlina.budgetplus.core.data.remote.User

internal data class InsiderData(
    val totalUsers: Long,
    val totalPremiumUsers: Long,
    val dailyActiveUsers: Long,
    val newUsers: List<User>,
)
