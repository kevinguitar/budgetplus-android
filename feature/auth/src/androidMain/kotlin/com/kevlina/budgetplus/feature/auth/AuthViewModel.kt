package com.kevlina.budgetplus.feature.auth

import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.lifecycleScope
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.auth_success
import co.touchlab.kermit.Logger
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.local.Preference
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

@Inject
internal class AuthViewModel(
    val navigation: NavigationFlow,
    val snackbarSender: SnackbarSender,
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    private val tracker: Tracker,
    private val activity: ComponentActivity,
    @Named("welcome") private val welcomeNavigationAction: NavigationAction,
    @Named("book") private val bookNavigationAction: NavigationAction,
    referrerHandler: ReferrerHandler,
    preference: Preference,
) {

    private val coroutineScope = activity.lifecycleScope

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val credentialManager by lazy { CredentialManager.create(activity) }
    private val googleClientId get() = activity.getString(R.string.google_cloud_client_id)

    private val isFirstLaunchAfterInstallKey = booleanPreferencesKey("isFirstLaunchAfterInstall")

    init {
        coroutineScope.launch {
            if (preference.of(isFirstLaunchAfterInstallKey).first() == null) {
                preference.update(isFirstLaunchAfterInstallKey, false)
                referrerHandler.retrieveReferrer()
            }
        }
    }

    fun signInWithGoogle() {
        val siwgOption = GetSignInWithGoogleOption
            .Builder(googleClientId)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(siwgOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                handleSignIn(result)
            } catch (e: GetCredentialCancellationException) {
                // Ignore cancellation exception
                Logger.d(e) { "Google sign in canceled" }
            } catch (e: GetCredentialException) {
                snackbarSender.sendError(e)
            }
        }

        tracker.logEvent("sign_in_with_google")
    }

    /**
     *  If there are any accounts that were authorized before, launch the sign in dialog.
     */
    fun checkAuthorizedAccounts(enableAutoSignIn: Boolean) {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setAutoSelectEnabled(enableAutoSignIn)
            .setServerClientId(googleClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                handleSignIn(result)
            } catch (e: GetCredentialCancellationException) {
                // Ignore cancellation exception
                Logger.d(e) { "Google sign in canceled" }
            } catch (e: NoCredentialException) {
                Logger.w(e) { "No credential is found" }
            } catch (e: GetCredentialException) {
                Logger.e(e) { "Fail to get credential" }
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential
        if (
            credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            coroutineScope.launch { snackbarSender.send("Unexpected type of credential") }
            Logger.e { "Unexpected type of credential. ${credential.type}" }
            return
        }

        val googleIdToken = try {
            GoogleIdTokenCredential.createFrom(credential.data).idToken
        } catch (e: GoogleIdTokenParsingException) {
            snackbarSender.sendError(e)
            return
        }

        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(activity, ::onLoginCompleted)
    }

    private fun onLoginCompleted(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            val isNewUser = task.result.additionalUserInfo?.isNewUser == true
            tracker.logEvent(if (isNewUser) "sign_up" else "login")

            coroutineScope.launch {
                val message = getString(Res.string.auth_success, task.result.user?.displayName.orEmpty())
                toaster.showMessage(message)
                checkBookAvailability()
            }
        } else {
            val e = task.exception ?: error("Unable to login")
            snackbarSender.sendError(e)
        }
    }

    private suspend fun checkBookAvailability() {
        val action = try {
            if (bookRepo.checkUserHasBook()) {
                bookNavigationAction
            } else {
                welcomeNavigationAction
            }
        } catch (e: Exception) {
            snackbarSender.sendError(e)
            welcomeNavigationAction
        }
        navigation.sendEvent(action)
    }
}