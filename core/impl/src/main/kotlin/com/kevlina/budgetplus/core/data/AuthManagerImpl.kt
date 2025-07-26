package com.kevlina.budgetplus.core.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Source
import com.google.firebase.messaging.messaging
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.data.remote.UsersDb
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class AuthManagerImpl @Inject constructor(
    preferenceHolder: PreferenceHolder,
    private val stringProvider: StringProvider,
    private val tracker: Lazy<Tracker>,
    @Named("allow_update_fcm_token") private val allowUpdateFcmToken: Boolean,
    @AppScope private val appScope: CoroutineScope,
    @UsersDb private val usersDb: Lazy<CollectionReference>,
) : AuthManager {

    private var currentUser by preferenceHolder.bindObjectOptional<User>()

    private val _userState = MutableStateFlow(currentUser)
    override val userState: StateFlow<User?> = _userState.asStateFlow()

    override val isPremium: StateFlow<Boolean> = userState.mapState { it?.premium == true }
    override val userId: String? get() = userState.value?.id

    init {
        Firebase.auth.addAuthStateListener { auth ->
            updateUser(auth.currentUser?.toUser())
        }
    }

    override fun requireUserId(): String {
        return requireNotNull(userId) { "User is null." }
    }

    override suspend fun renameUser(newName: String) {
        val currentUser = Firebase.auth.currentUser ?: error("Current user is null.")

        currentUser.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
        ).await()

        updateUser(
            user = currentUser.toUser().copy(name = newName),
            newName = newName
        )
        tracker.get().logEvent("user_renamed")
    }

    override fun markPremium() {
        val premiumUser = currentUser?.copy(premium = true) ?: return
        usersDb.get().document(premiumUser.id).set(premiumUser)

        _userState.value = premiumUser
        currentUser = premiumUser
    }

    override fun updateFcmToken(newToken: String) {
        if (!allowUpdateFcmToken) return

        val userWithNewToken = currentUser?.copy(fcmToken = newToken) ?: return
        usersDb.get().document(userWithNewToken.id).set(userWithNewToken)

        _userState.value = userWithNewToken
        currentUser = userWithNewToken
    }

    override fun logout() {
        tracker.get().logEvent("logout")
        Firebase.auth.signOut()
    }

    private fun FirebaseUser.toUser() = User(
        id = uid,
        name = displayName,
        photoUrl = photoUrl?.toString(),
    )

    private fun updateUser(user: User?, newName: String? = null) {
        if (user == null) {
            _userState.value = null
            currentUser = null
            return
        }

        // Associate the crash report with Budget+ user
        Firebase.crashlytics.setUserId(user.id)

        val userWithExclusiveFields = user.copy(
            premium = currentUser?.premium,
            createdOn = currentUser?.createdOn ?: System.currentTimeMillis(),
            lastActiveOn = System.currentTimeMillis(),
            language = stringProvider[R.string.app_language],
        )
        _userState.value = userWithExclusiveFields
        currentUser = userWithExclusiveFields

        appScope.launch {
            val fcmToken = if (allowUpdateFcmToken) {
                try {
                    Firebase.messaging.token.await()
                } catch (e: Exception) {
                    Timber.w(e, "Failed to retrieve the fcm token")
                    null
                }
            } else {
                null
            }
            Timber.d("Fcm token: $fcmToken")

            try {
                // Get the latest remote user from the server
                val remoteUser = usersDb.get().document(user.id)
                    .get(Source.SERVER)
                    .requireValue<User>()

                // Merge exclusive fields to the Firebase auth user
                val mergedUser = userWithExclusiveFields.copy(
                    name = newName ?: remoteUser.name,
                    premium = remoteUser.premium,
                    internal = remoteUser.internal ?: false,
                    createdOn = remoteUser.createdOn,
                    fcmToken = fcmToken ?: remoteUser.fcmToken
                )

                _userState.value = mergedUser
                currentUser = mergedUser

                usersDb.get().document(user.id).set(mergedUser)
            } catch (e: DocNotExistsException) {
                Timber.i(e)
                // Can't find user in the db yet, set it with the data what we have in place.
                usersDb.get().document(user.id)
                    .set(userWithExclusiveFields.copy(fcmToken = fcmToken))
            } catch (e: Exception) {
                Timber.w(e)
            }
        }
    }
}