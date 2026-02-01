package com.kevlina.budgetplus.core.data

import androidx.datastore.preferences.core.stringPreferencesKey
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.app_language
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
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.data.local.Preference
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.data.remote.UsersDb
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AuthManagerImpl(
    private val preference: Preference,
    private val tracker: Lazy<Tracker>,
    @Named("allow_update_fcm_token") private val allowUpdateFcmToken: Boolean,
    @AppCoroutineScope private val appScope: CoroutineScope,
    @UsersDb private val usersDb: Lazy<CollectionReference>,
) : AuthManager {

    private val currentUserKey = stringPreferencesKey("currentUser")
    private val currentUserFlow = preference.of(currentUserKey, User.serializer())

    final override val userState: StateFlow<User?> = currentUserFlow
        .filterNotNull()
        .stateIn(
            scope = appScope,
            started = SharingStarted.Eagerly,
            // Critical default value for app start
            initialValue = runBlocking { currentUserFlow.first() }
        )
    private val currentUser: User? get() = userState.value

    override val isPremium: StateFlow<Boolean> = userState.mapState { it?.premium == true }
    override val userId: String? get() = userState.value?.id

    init {
        appScope.launch { userState.collect() }

        Firebase.auth.addAuthStateListener { auth ->
            runBlocking { updateUser(auth.currentUser?.toUser()) }
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

    override suspend fun markPremium() {
        val premiumUser = currentUser?.copy(premium = true) ?: return
        usersDb.value.document(premiumUser.id).set(premiumUser)
        setUserToPreference(premiumUser)
    }

    override suspend fun updateFcmToken(newToken: String) {
        if (!allowUpdateFcmToken) return

        val userWithNewToken = currentUser?.copy(fcmToken = newToken) ?: return
        usersDb.value.document(userWithNewToken.id).set(userWithNewToken)
        setUserToPreference(userWithNewToken)
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

    private suspend fun updateUser(user: User?, newName: String? = null) {
        if (user == null) {
            setUserToPreference(null)
            return
        }

        // Associate the crash report with Budget+ user
        Firebase.crashlytics.setUserId(user.id)

        val userWithExclusiveFields = user.copy(
            premium = currentUser?.premium,
            createdOn = currentUser?.createdOn ?: Clock.System.now().toEpochMilliseconds(),
            lastActiveOn = Clock.System.now().toEpochMilliseconds(),
            language = getString(Res.string.app_language),
        )
        setUserToPreference(userWithExclusiveFields)

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
                setUserToPreference(mergedUser)

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

    private suspend fun setUserToPreference(user: User?) {
        if (user == null) {
            preference.remove(currentUserKey)
        } else {
            preference.update(currentUserKey, User.serializer(), user)
        }
    }
}