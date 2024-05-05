package com.kevlina.budgetplus.feature.auth

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
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
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    private val tracker: Tracker,
    private val stringProvider: StringProvider,
    @ActivityContext private val context: Context,
    @Named("welcome") private val welcomeNavigationAction: NavigationAction,
    @Named("book") private val bookNavigationAction: NavigationAction,
    @Named("contact_email") private val contactEmail: String,
    referrerHandler: ReferrerHandler,
    preferenceHolder: PreferenceHolder,
) {

    val navigation = NavigationFlow()

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
            enableAutoSignIn = true
        )
        tracker.logEvent("sign_in_with_google")
    }

    /**
     *  If there are any accounts that were authorized before, launch the sign in dialog.
     */
    fun checkAuthorizedAccounts(enableAutoSignIn: Boolean) {
        launchGoogleSignIn(filterByAuthorizedAccounts = true, enableAutoSignIn = enableAutoSignIn)
    }

    private fun launchGoogleSignIn(
        filterByAuthorizedAccounts: Boolean,
        enableAutoSignIn: Boolean,
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
            } catch (e: GetCredentialException) {
                toaster.showError(e)
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential
        if (
            credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            toaster.showMessage("Unexpected type of credential")
            Timber.e("Unexpected type of credential. ${credential.type}")
            return
        }

        val googleIdToken = try {
            GoogleIdTokenCredential.createFrom(credential.data).idToken
        } catch (e: GoogleIdTokenParsingException) {
            toaster.showError(e)
            return
        }

        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(activity, ::onLoginCompleted)
    }

    fun onContactClick() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(contactEmail))
            putExtra(Intent.EXTRA_SUBJECT, stringProvider[R.string.settings_contact_us])
            putExtra(Intent.EXTRA_TEXT, stringProvider[R.string.auth_facebook_contact_text])
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            tracker.logEvent("auth_contact_us_click")
        } else {
            toaster.showMessage(R.string.settings_no_email_app_found)
        }
    }

    private fun onLoginCompleted(task: Task<AuthResult>) {
        when {
            task.isSuccessful -> {
                val isNewUser = task.result.additionalUserInfo?.isNewUser == true
                tracker.logEvent(if (isNewUser) "sign_up" else "login")

                val message = stringProvider[R.string.auth_success, task.result.user?.displayName.orEmpty()]
                toaster.showMessage(message)
                checkBookAvailability()
            }

            task.isCanceled -> Timber.d("Google sign in canceled")
            else -> toaster.showError(task.exception ?: error("Unable to login"))
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
                toaster.showError(e)
                welcomeNavigationAction
            }
            navigation.sendEvent(action)
        }
    }
}