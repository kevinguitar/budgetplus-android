package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.User

interface InsiderRepo {

    suspend fun getTotalUsers(): Long

    suspend fun getTotalPremiumUsers(): Long

    suspend fun getTotalUsersByLanguage(language: String): Long

    suspend fun getDailyActiveUsers(): Long

    suspend fun getActivePremiumUsers(count: Int): List<User>

    suspend fun getNewUsers(count: Int): List<User>

}