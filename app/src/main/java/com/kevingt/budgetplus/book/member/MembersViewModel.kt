package com.kevingt.budgetplus.book.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kevingt.budgetplus.auth.AuthManager
import com.kevingt.budgetplus.data.remote.BookRepo
import com.kevingt.budgetplus.data.remote.User
import com.kevingt.budgetplus.utils.Toaster
import com.kevingt.budgetplus.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MembersViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val authManager: AuthManager,
    private val toaster: Toaster
) : ViewModel() {

    private val usersDb = Firebase.firestore.collection("users")

    val bookMembers = MutableStateFlow<List<User>>(emptyList())

    val userId get() = authManager.requireUserId()

    val ownerId get() = bookRepo.bookState.value?.ownerId
    val bookName get() = bookRepo.bookState.value?.name

//    init {
//        loadMembers()
//    }

    fun removeMember(userId: String) {
        viewModelScope.launch {
            try {
                bookRepo.removeMember(userId)
                loadMembers()
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun loadMembers() {
        val authors = bookRepo.bookState.value?.authors
        if (authors != null) {
            viewModelScope.launch {
                try {
                    val users = authors
                        .map { id -> async { getUser(id) } }
                        .awaitAll()
                        .toMutableList()

                    // Move the owner to the first of the list
                    val owner = users.find { it.id == ownerId }
                    if (owner != null) {
                        users.remove(owner)
                        users.add(0, owner)
                    }

                    bookMembers.value = users
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    private suspend fun getUser(userId: String): User {
        return usersDb.document(userId).get().requireValue()
    }
}