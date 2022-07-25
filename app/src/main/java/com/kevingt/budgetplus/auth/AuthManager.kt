package com.kevingt.budgetplus.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kevingt.budgetplus.data.local.PreferenceHolder
import com.kevingt.budgetplus.data.remote.User
import com.kevingt.budgetplus.utils.AppScope
import com.kevingt.budgetplus.utils.await
import com.kevingt.budgetplus.utils.mapState
import com.kevingt.budgetplus.utils.requireValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface AuthManager {

    val userState: StateFlow<User?>
    val isPremium: StateFlow<Boolean>
    val isHideAds: StateFlow<Boolean>

    fun requireUserId(): String

    suspend fun renameUser(newName: String)

    fun logout()

}

@Singleton
class AuthManagerImpl @Inject constructor(
    preferenceHolder: PreferenceHolder,
    @AppScope appScope: CoroutineScope
) : AuthManager {

    private var currentUser by preferenceHolder.bindObjectOptional<User>()

    private val usersDb = Firebase.firestore.collection("users")

    private val _userState = MutableStateFlow(currentUser)
    override val userState: StateFlow<User?> = _userState.asStateFlow()

    override val isPremium: StateFlow<Boolean> = userState.mapState { it?.premium == true }
    override val isHideAds: StateFlow<Boolean> = userState.mapState { it?.hideAds == true }

    init {
        Firebase.auth.addAuthStateListener { auth ->
            appScope.launch {
                updateUser(auth.currentUser?.toUser())
            }
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

        updateUser(currentUser.toUser().copy(name = newName))
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

    private suspend fun updateUser(user: User?) {
        if (user == null) {
            _userState.value = null
            currentUser = null
            return
        }

        // Get the latest remote user from the server
        val remoteUser = usersDb.document(user.id)
            .get(Source.SERVER)
            .requireValue<User>()

        // Merge exclusive fields to the Firebase auth user
        val mergedUser = user.copy(
            premium = remoteUser.premium,
            hideAds = remoteUser.hideAds
        )

        _userState.value = mergedUser
        currentUser = mergedUser
        usersDb.document(user.id).set(mergedUser)
    }
}