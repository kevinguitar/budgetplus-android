package com.kevlina.budgetplus.feature.auth

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class AuthViewModel @Inject constructor(
    val navigation: NavigationFlow,
    val snackbarSender: SnackbarSender,
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    private val tracker: Tracker,
    private val stringProvider: StringProvider,
    @ActivityContext private val context: Context,
    @Named("welcome") private val welcomeNavigationAction: NavigationAction,
    @Named("book") private val bookNavigationAction: NavigationAction,
    referrerHandler: ReferrerHandler,
    preferenceHolder: PreferenceHolder,
) {

    private val activity = context as ComponentActivity
    private val coroutineScope = activity.lifecycleScope

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val credentialManager by lazy { CredentialManager.create(activity) }

    private var isFirstLaunchAfterInstall by preferenceHolder.bindBoolean(true)

    init {
        if (isFirstLaunchAfterInstall) {
            isFirstLaunchAfterInstall = false
            referrerHandler.retrieveReferrer()
        }
    }

    fun signInWithGoogle() {
        launchGoogleSignIn(
            filterByAuthorizedAccounts = false,
            enableAutoSignIn = true,
            displayError = true
        )
        tracker.logEvent("sign_in_with_google")
    }

    /**
     *  If there are any accounts that were authorized before, launch the sign in dialog.
     */
    fun checkAuthorizedAccounts(enableAutoSignIn: Boolean) {
        launchGoogleSignIn(
            filterByAuthorizedAccounts = true,
            enableAutoSignIn = enableAutoSignIn,
            displayError = false
        )
    }

    private fun launchGoogleSignIn(
        filterByAuthorizedAccounts: Boolean,
        enableAutoSignIn: Boolean,
        displayError: Boolean,
    ) {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setAutoSelectEnabled(enableAutoSignIn)
            .setServerClientId(stringProvider[R.string.google_cloud_client_id])
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
                Timber.d(e, "Google sign in canceled")
            } catch (e: NoCredentialException) {
                Timber.w(e, "No credential is found")
                if (displayError) {
                    snackbarSender.send(R.string.auth_no_credential_found, canDismiss = true)
                }
            } catch (e: GetCredentialException) {
                if (displayError) {
                    snackbarSender.sendError(e)
                } else {
                    Timber.e(e, "Fail to get credential")
                }
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential
        if (
            credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            snackbarSender.send("Unexpected type of credential", canDismiss = true)
            Timber.e("Unexpected type of credential. ${credential.type}")
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

            val message = stringProvider[R.string.auth_success, task.result.user?.displayName.orEmpty()]
            toaster.showMessage(message)
            checkBookAvailability()
        } else {
            val e = task.exception ?: error("Unable to login")
            snackbarSender.sendError(e)
        }
    }

    private fun checkBookAvailability() {
        coroutineScope.launch {
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
}