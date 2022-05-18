package com.kevingt.moneybook.auth.ui

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
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
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kevingt.moneybook.R
import com.kevingt.moneybook.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

const val REQ_ONE_TAP = 134

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    private val viewModel by viewModels<AuthViewModel>()

    private val oneTapClient: SignInClient by lazy { Identity.getSignInClient(this) }
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    private val callbackManager = CallbackManager.Factory.create()

    private val callback = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult) {
            handleFacebookAccessToken(result.accessToken)
        }

        override fun onCancel() = Timber.w("Facebook sign-in canceled")

        override fun onError(error: FacebookException) {
            Timber.e(error, "Facebook sign-in failed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
//            AuthBinding(viewModel = viewModel)
            Column {
                Button(onClick = { signInWithFacebook() }) {
                    Text(text = "Facebook")
                }

                Button(onClick = { signInWithGoogle() }) {
                    Text(text = "Google")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken

                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = auth.currentUser
                                Toast.makeText(this, "Google Login success!!!", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                // If sign in fails, display a message to the user.
                                Timber.e(task.exception)
                            }
                        }

                } catch (e: ApiException) {
                    Timber.e(e)
                }
            }
        }
    }

    private fun signInWithFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, callback)
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("email", "public_profile")
            )
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        this, "Authentication success.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val user = auth.currentUser
                } else {
                    Timber.e(task.exception)
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val request = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.google_cloud_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        oneTapClient.beginSignIn(request)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Timber.e("Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Timber.e(e.localizedMessage)
            }
    }
}