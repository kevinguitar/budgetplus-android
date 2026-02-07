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
import co.touchlab.kermit.Logger
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.data.local.Preference
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Inject
internal class AndroidAuthViewModel(
    val commonAuth: CommonAuthViewModel,
    private val activity: ComponentActivity,
    private val snackbarSender: SnackbarSender,
    referrerHandler: ReferrerHandler,
    preference: Preference,
) {
    private val coroutineScope = activity.lifecycleScope

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

        coroutineScope.launch {
            commonAuth.proceedGoogleSignInWithIdToken(googleIdToken, accessToken = null)
        }
    }
}