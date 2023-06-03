package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.User

interface InsiderRepo {

    suspend fun getTotalUsers(): Long

    suspend fun getTotalPremiumUsers(): Long

    suspend fun getDailyActiveUsers(): Long

    suspend fun getNewUsers(count: Int): List<User>

}