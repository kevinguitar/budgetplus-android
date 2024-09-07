package com.kevlina.budgetplus.feature.insider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.data.InsiderRepo
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.ui.SnackbarSender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class InsiderViewModel @Inject constructor(
    private val insiderRepo: InsiderRepo,
    private val snackbarSender: SnackbarSender,
) : ViewModel() {

    val insiderData: StateFlow<InsiderData?> = ::getInsiderData.asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val usersOverviewData: StateFlow<UsersOverviewData?> = ::getUsersOverviewData.asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val newUsers: StateFlow<List<User>?> = ::getNewUser.asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val activePremiumUsers: StateFlow<List<User>?> = ::getActivePremiumUser.asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private suspend fun getInsiderData(): InsiderData? = try {
        coroutineScope {
            val totalPremiumUsers = async { insiderRepo.getTotalPremiumUsers() }
            val dailyActiveUsers = async { insiderRepo.getDailyActiveUsers() }

            InsiderData(
                totalPremiumUsers = totalPremiumUsers.await(),
                dailyActiveUsers = dailyActiveUsers.await(),
            )
        }
    } catch (e: Exception) {
        snackbarSender.sendError(e)
        null
    }

    private suspend fun getUsersOverviewData(): UsersOverviewData? = try {
        coroutineScope {
            val totalUsers = async { insiderRepo.getTotalUsers() }
            val totalEnglishUsers = async { insiderRepo.getTotalUsersByLanguage("en") }
            val totalJapaneseUsers = async { insiderRepo.getTotalUsersByLanguage("ja") }
            val totalSimplifiedChineseUsers = async { insiderRepo.getTotalUsersByLanguage("zh-cn") }

            UsersOverviewData(
                totalUsers = totalUsers.await(),
                totalEnglishUsers = totalEnglishUsers.await(),
                totalJapaneseUsers = totalJapaneseUsers.await(),
                totalSimplifiedChineseUsers = totalSimplifiedChineseUsers.await(),
            )
        }
    } catch (e: Exception) {
        snackbarSender.sendError(e)
        null
    }

    private suspend fun getNewUser(): List<User>? = try {
        insiderRepo.getNewUsers(NEW_USERS_COUNT)
    } catch (e: Exception) {
        snackbarSender.sendError(e)
        null
    }

    private suspend fun getActivePremiumUser(): List<User>? = try {
        insiderRepo.getActivePremiumUsers(ACTIVE_PREMIUM_USERS_COUNT)
    } catch (e: Exception) {
        snackbarSender.sendError(e)
        null
    }

    private companion object {
        const val NEW_USERS_COUNT = 5
        const val ACTIVE_PREMIUM_USERS_COUNT = 20
    }
}