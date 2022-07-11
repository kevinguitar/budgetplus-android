package com.kevingt.moneybook.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kevingt.moneybook.data.local.PreferenceHolder
import com.kevingt.moneybook.data.remote.User
import com.kevingt.moneybook.utils.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface AuthManager {

    val userState: StateFlow<User?>

    fun requireUserId(): String

    suspend fun renameUser(newName: String)

    fun logout()

}

@Singleton
class AuthManagerImpl @Inject constructor(
    preferenceHolder: PreferenceHolder,
) : AuthManager {

    private var currentUser by preferenceHolder.bindObjectOptional<User>()

    private val usersDb = Firebase.firestore.collection("users")

    private val _userState = MutableStateFlow(currentUser)
    override val userState: StateFlow<User?> get() = _userState

    init {
        Firebase.auth.addAuthStateListener { auth ->
            setUser(auth.currentUser?.toUser())
        }
    }

    override fun requireUserId(): String {
        return requireNotNull(userState.value?.id) { "User is null." }
    }

    override suspend fun renameUser(newName: String) {
        val currentUser = Firebase.auth.currentUser
            ?: throw IllegalStateException("Current user is null.")

        currentUser.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
        ).await()

        setUser(currentUser.toUser().copy(name = newName))
    }

    override fun logout() {
        Firebase.auth.signOut()
    }

    private fun FirebaseUser.toUser() = User(
        id = uid,
        name = displayName,
        email = email,
        photoUrl = photoUrl?.toString()
    )

    private fun setUser(user: User?) {
        _userState.value = user
        currentUser = user

        if (user != null) {
            usersDb.document(user.id).set(user)
        }
    }
}