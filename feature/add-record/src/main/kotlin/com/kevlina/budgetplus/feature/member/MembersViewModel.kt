package com.kevlina.budgetplus.feature.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.UserRepo
import com.kevlina.budgetplus.core.data.remote.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MembersViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val authManager: AuthManager,
    private val userRepo: UserRepo,
    private val toaster: Toaster,
    private val tracker: Tracker
) : ViewModel() {

    val bookMembers = MutableStateFlow<List<User>>(emptyList())

    val userId get() = authManager.requireUserId()

    val ownerId get() = bookRepo.bookState.value?.ownerId
    val bookName get() = bookRepo.bookState.value?.name

    fun removeMember(userId: String) {
        viewModelScope.launch {
            try {
                bookRepo.removeMember(userId)
                tracker.logEvent("member_removed")
                loadMembers()
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun loadMembers() {
        val authors = bookRepo.bookState.value?.authors ?: return
        val users = authors
            .mapNotNull { id -> userRepo.getUser(id) }
            .toMutableList()

        // Move the owner to the first of the list
        val owner = users.find { it.id == ownerId }
        if (owner != null) {
            users.remove(owner)
            users.add(0, owner)
        }

        bookMembers.value = users
        tracker.logEvent("member_loaded")
    }
}