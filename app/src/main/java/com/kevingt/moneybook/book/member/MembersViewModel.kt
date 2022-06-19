package com.kevingt.moneybook.book.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.User
import com.kevingt.moneybook.utils.Toaster
import com.kevingt.moneybook.utils.mapState
import com.kevingt.moneybook.utils.requireValue
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

    val bookName get() = bookRepo.bookState.value?.name

    val isBookOwner = bookRepo.bookState.mapState { it?.ownerId == userId }

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
                    bookMembers.value = authors
                        .map { id -> async { getUser(id) } }
                        .awaitAll()
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