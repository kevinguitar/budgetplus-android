package com.kevlina.budgetplus.core.data

import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.anonymous_user
import com.kevlina.budgetplus.core.data.remote.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.getString

@ContributesBinding(AppScope::class)
internal class AuthStateImpl : AuthState {

    override val authStateChanged: Flow<User?> =
        Firebase.auth.authStateChanged.map { firebaseUser ->
            firebaseUser?.let {
                User(
                    id = it.uid,
                    name = it.displayName ?: getString(Res.string.anonymous_user),
                    photoUrl = it.photoURL,
                )
            }
        }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun updateCurrentUserProfile(displayName: String) {
        val currentUser = Firebase.auth.currentUser ?: error("Current user is null.")
        currentUser.updateProfile(displayName = displayName)
    }
}
