package com.kevlina.budgetplus.core.data.impl

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.DocNotExistsException
import com.kevlina.budgetplus.core.data.await
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.data.requireValue
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthManagerImpl @Inject constructor(
    preferenceHolder: PreferenceHolder,
    private val gso: dagger.Lazy<GoogleSignInOptions>,
    @AppScope private val appScope: CoroutineScope,
    @ApplicationContext private val context: Context
) : AuthManager {

    private var currentUser by preferenceHolder.bindObjectOptional<User>()

    private val usersDb by lazy { Firebase.firestore.collection("users") }

    private val _userState = MutableStateFlow(currentUser)
    override val userState: StateFlow<User?> = _userState.asStateFlow()

    override val isPremium: StateFlow<Boolean> = userState.mapState { it?.premium == true }

    init {
        Firebase.auth.addAuthStateListener { auth ->
            updateUser(auth.currentUser?.toUser())
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

        updateUser(
            user = currentUser.toUser().copy(name = newName),
            newName = newName
        )
    }

    override fun markPremium() {
        val premiumUser = currentUser?.copy(premium = true) ?: return
        usersDb.document(premiumUser.id).set(premiumUser)

        _userState.value = premiumUser
        currentUser = premiumUser
    }

    override fun logout() {
        Firebase.auth.signOut()
        GoogleSignIn.getClient(context, gso.get()).signOut()
    }

    private fun FirebaseUser.toUser() = User(
        id = uid,
        name = displayName,
        email = email,
        photoUrl = photoUrl?.toString(),
    )

    private fun updateUser(user: User?, newName: String? = null) {
        if (user == null) {
            _userState.value = null
            currentUser = null
            return
        }

        val userWithExclusiveFields = user.copy(
            premium = currentUser?.premium,
            createdOn = currentUser?.createdOn ?: System.currentTimeMillis(),
            lastActiveOn = System.currentTimeMillis()
        )
        _userState.value = userWithExclusiveFields
        currentUser = userWithExclusiveFields

        appScope.launch {
            try {
                // Get the latest remote user from the server
                val remoteUser = usersDb.document(user.id)
                    .get(Source.SERVER)
                    .requireValue<User>()

                // Merge exclusive fields to the Firebase auth user
                val mergedUser = userWithExclusiveFields.copy(
                    name = newName ?: remoteUser.name,
                    premium = remoteUser.premium,
                    createdOn = remoteUser.createdOn
                )

                _userState.value = mergedUser
                currentUser = mergedUser

                usersDb.document(user.id).set(mergedUser)
            } catch (e: DocNotExistsException) {
                // Can't find user in the db yet, set it with the local data.
                usersDb.document(user.id).set(userWithExclusiveFields)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}