package com.kevlina.budgetplus.book.di

import GoogleSignIn.GIDSignIn
import com.kevlina.budgetplus.feature.auth.IosGoogleSignInProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIApplication
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@ContributesBinding(AppScope::class)
class IosGoogleSignInProviderImpl : IosGoogleSignInProvider {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun signInWithGoogle(): IosGoogleSignInProvider.Result {
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            ?: error("No root view controller")
        return suspendCancellableCoroutine { cont ->
            GIDSignIn.sharedInstance.signInWithPresentingViewController(rootViewController) { result, error ->
                if (error != null) {
                    cont.resumeWithException(RuntimeException(error.localizedDescription))
                }

                val idToken = result?.user?.idToken?.tokenString
                if (idToken == null) {
                    cont.resumeWithException(RuntimeException("No ID token"))
                } else {
                    cont.resume(IosGoogleSignInProvider.Result(idToken))
                }
            }
        }
    }
}