package com.kevlina.budgetplus.feature.auth

interface IosGoogleSignInProvider {
    suspend fun signInWithGoogle(): Result

    data class Result(
        val idToken: String,
        val accessToken: String?
    )
}