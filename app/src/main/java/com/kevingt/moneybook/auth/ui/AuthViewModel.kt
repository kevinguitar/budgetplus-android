package com.kevingt.moneybook.auth.ui

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.activity.ComponentActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kevingt.moneybook.R
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.utils.Toaster
import dagger.hilt.android.qualifiers.ActivityContext
import timber.log.Timber
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val toaster: Toaster,
    @ActivityContext private val context: Context,
) {

    private val activity = context as ComponentActivity
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val oneTapClient: SignInClient by lazy { Identity.getSignInClient(activity) }
    private val callbackManager by lazy { CallbackManager.Factory.create() }

    private val callback = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult) {
            handleFacebookAccessToken(result.accessToken)
        }

        override fun onCancel() = Timber.w("Facebook sign-in canceled")

        override fun onError(error: FacebookException) {
            Timber.e(error, "Facebook sign-in failed")
        }
    }

    fun signInWithFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, callback)
        LoginManager.getInstance().logInWithReadPermissions(
            activity,
            listOf("email", "public_profile")
        )
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener(activity, ::onLoginCompleted)
    }

    fun signInWithGoogle() {
        val request = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.google_cloud_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        oneTapClient.beginSignIn(request)
            .addOnSuccessListener(activity) { result ->
                try {
                    @Suppress("DEPRECATION")
                    activity.startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Timber.e("Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(activity) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                toaster.showError(e)
            }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_ONE_TAP) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(activity, ::onLoginCompleted)

            } catch (e: ApiException) {
                toaster.showError(e)
            }
        }
    }

    private fun onLoginCompleted(task: Task<AuthResult>) {
        val user = auth.currentUser
        if (task.isSuccessful && user != null) {
            authManager.setUser(user)
            toaster.showMessage("Login success.")
            //TODO: navigate
        } else {
            val e = task.exception ?: IllegalStateException("Unable to login")
            toaster.showError(e)
        }
    }

    companion object {
        private const val REQ_ONE_TAP = 134
    }
}