package com.kevlina.budgetplus.core.data

import co.touchlab.kermit.Logger
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Source
import com.google.firebase.messaging.messaging
import com.kevlina.budgetplus.core.common.AppCoroutineScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.data.remote.UsersDb
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.time.Clock

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AuthManagerImpl(
    preferenceHolder: PreferenceHolder,
    private val stringProvider: StringProvider,
    private val tracker: Lazy<Tracker>,
    @Named("allow_update_fcm_token") private val allowUpdateFcmToken: Boolean,
    @AppCoroutineScope private val appScope: CoroutineScope,
    @UsersDb private val usersDb: Lazy<CollectionReference>,
) : AuthManager {

    private var currentUser by preferenceHolder.bindObjectOptional<User>()

    final override val userState: StateFlow<User?>
        field = MutableStateFlow(currentUser)

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
        tracker.value.logEvent("user_renamed")
    }

    override fun markPremium() {
        val premiumUser = currentUser?.copy(premium = true) ?: return
        usersDb.value.document(premiumUser.id).set(premiumUser)

        userState.value = premiumUser
        currentUser = premiumUser
    }

    override fun updateFcmToken(newToken: String) {
        if (!allowUpdateFcmToken) return

        val userWithNewToken = currentUser?.copy(fcmToken = newToken) ?: return
        usersDb.value.document(userWithNewToken.id).set(userWithNewToken)

        userState.value = userWithNewToken
        currentUser = userWithNewToken
    }

    override fun logout() {
        tracker.value.logEvent("logout")
        Firebase.auth.signOut()
    }

    private fun FirebaseUser.toUser() = User(
        id = uid,
        name = displayName,
        photoUrl = photoUrl?.toString(),
    )

    private fun updateUser(user: User?, newName: String? = null) {
        if (user == null) {
            userState.value = null
            currentUser = null
            return
        }

        // Associate the crash report with Budget+ user
        Firebase.crashlytics.setUserId(user.id)

        val userWithExclusiveFields = user.copy(
            premium = currentUser?.premium,
            createdOn = currentUser?.createdOn ?: Clock.System.now().toEpochMilliseconds(),
            lastActiveOn = Clock.System.now().toEpochMilliseconds(),
            language = stringProvider[R.string.app_language],
        )
        userState.value = userWithExclusiveFields
        currentUser = userWithExclusiveFields

        appScope.launch {
            val fcmToken = if (allowUpdateFcmToken) {
                try {
                    Firebase.messaging.token.await()
                } catch (e: Exception) {
                    Logger.w(e) { "Failed to retrieve the fcm token" }
                    null
                }
            } else {
                null
            }
            Logger.d { "Fcm token: $fcmToken" }

            try {
                // Get the latest remote user from the server
                val remoteUser = usersDb.value.document(user.id)
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

                userState.value = mergedUser
                currentUser = mergedUser

                usersDb.value.document(user.id).set(mergedUser)
            } catch (e: DocNotExistsException) {
                Logger.i(e) { "DocNotExistsException caught" }
                // Can't find user in the db yet, set it with the data what we have in place.
                usersDb.value.document(user.id)
                    .set(userWithExclusiveFields.copy(fcmToken = fcmToken))
            } catch (e: Exception) {
                Logger.w(e) { "AuthManager update failed" }
            }
        }
    }
}