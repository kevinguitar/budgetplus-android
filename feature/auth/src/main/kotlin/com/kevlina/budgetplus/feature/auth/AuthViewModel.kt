package com.kevlina.budgetplus.feature.auth

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Stable
import androidx.lifecycle.lifecycleScope
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
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

@Stable
class AuthViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    private val tracker: Tracker,
    private val stringProvider: StringProvider,
    private val gso: dagger.Lazy<GoogleSignInOptions>,
    @ActivityContext private val context: Context,
    @Named("welcome") private val welcomeNavigationAction: NavigationAction,
    @Named("book") private val bookNavigationAction: NavigationAction,
    referrerHandler: ReferrerHandler,
    preferenceHolder: PreferenceHolder,
) {

    val navigation = NavigationFlow()

    private val activity = context as ComponentActivity
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val oneTapClient: SignInClient by lazy { Identity.getSignInClient(activity) }
    private val callbackManager by lazy { CallbackManager.Factory.create() }
    private val googleSignInClient by lazy { GoogleSignIn.getClient(activity, gso.get()) }

    private val callback = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult) {
            handleFacebookAccessToken(result.accessToken)
        }

        override fun onCancel() = Timber.d("Facebook sign-in canceled")

        override fun onError(error: FacebookException) {
            Timber.e(error, "Facebook sign-in failed")
        }
    }

    private var isFirstLaunchAfterInstall by preferenceHolder.bindBoolean(true)

    init {
        if (isFirstLaunchAfterInstall) {
            isFirstLaunchAfterInstall = false
            referrerHandler.retrieveReferrer()
        }
    }

    fun signInWithFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, callback)
        LoginManager.getInstance().logInWithReadPermissions(
            activity,
            listOf("email", "public_profile")
        )
        tracker.logEvent("sign_in_with_facebook")
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener(activity, ::onLoginCompleted)
    }

    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        @Suppress("DEPRECATION")
        activity.startActivityForResult(signInIntent, REQ_GOOGLE_SIGN_IN)
        tracker.logEvent("sign_in_with_google")
    }

    /**
     *  If there are any accounts that were authorized before, launch the one-tap sign in dialog.
     */
    fun checkAuthorizedAccounts() {
        val request = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(stringProvider[R.string.google_cloud_client_id])
                    .setFilterByAuthorizedAccounts(true)
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
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Pass the activity result back to the Facebook SDK
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return
        }

        when {
            requestCode == REQ_ONE_TAP -> try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(activity, ::onLoginCompleted)
            } catch (e: ApiException) {
                if (e.statusCode == CommonStatusCodes.CANCELED) {
                    Timber.d("One tap login cancelled.")
                } else {
                    toaster.showError(e)
                }
            }

            requestCode == REQ_GOOGLE_SIGN_IN && resultCode == RESULT_OK -> try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val firebaseCredential = GoogleAuthProvider.getCredential(task.result.idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(activity, ::onLoginCompleted)
            } catch (e: ApiException) {
                Timber.w(e)
            }
        }
    }

    private fun onLoginCompleted(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            val isNewUser = task.result.additionalUserInfo?.isNewUser == true
            tracker.logEvent(if (isNewUser) "sign_up" else "login")

            val message = stringProvider[R.string.auth_success, task.result.user?.displayName.orEmpty()]
            toaster.showMessage(message)
            checkBookAvailability()
        } else {
            val e = task.exception ?: IllegalStateException("Unable to login")
            toaster.showError(e)
        }
    }

    private fun checkBookAvailability() {
        activity.lifecycleScope.launch {
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

    companion object {
        private const val REQ_ONE_TAP = 134
        private const val REQ_GOOGLE_SIGN_IN = 135
    }
}