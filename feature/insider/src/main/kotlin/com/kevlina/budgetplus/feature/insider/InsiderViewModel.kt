package com.kevlina.budgetplus.feature.insider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.data.InsiderRepo
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
    private val toaster: Toaster,
) : ViewModel() {

    val insiderData: StateFlow<InsiderData?> = ::getInsiderData.asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private suspend fun getInsiderData(): InsiderData? = try {
        coroutineScope {
            val totalUsers = async { insiderRepo.getTotalUsers() }
            val totalPremiumUsers = async { insiderRepo.getTotalPremiumUsers() }
            val dailyActiveUsers = async { insiderRepo.getDailyActiveUsers() }
            val newUsers = async { insiderRepo.getNewUsers(10) }

            InsiderData(
                totalUsers = totalUsers.await(),
                totalPremiumUsers = totalPremiumUsers.await(),
                dailyActiveUsers = dailyActiveUsers.await(),
                newUsers = newUsers.await()
            )
        }
    } catch (e: Exception) {
        toaster.showError(e)
        null
    }
}