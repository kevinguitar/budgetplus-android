package com.kevlina.budgetplus.feature.insider

import androidx.compose.runtime.Immutable
import com.kevlina.budgetplus.core.data.remote.User

@Immutable
internal data class InsiderData(
    val totalUsers: Long,
    val totalPremiumUsers: Long,
    val dailyActiveUsers: Long,
    val newUsers: List<User>,
)
