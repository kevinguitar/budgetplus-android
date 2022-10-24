package com.kevlina.budgetplus.auth

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse
import com.android.installreferrer.api.InstallReferrerStateListener
import com.kevlina.budgetplus.core.data.BookRepo
import dagger.hilt.android.qualifiers.ActivityContext
import timber.log.Timber
import javax.inject.Inject

class ReferrerHandler @Inject constructor(
    @ActivityContext private val context: Context,
    private val bookRepo: BookRepo
) : InstallReferrerStateListener {

    private val referrerClient by lazy {
        InstallReferrerClient
            .newBuilder(context)
            .build()
    }

    fun retrieveReferrer() {
        referrerClient.startConnection(this)
    }

    override fun onInstallReferrerSetupFinished(responseCode: Int) {
        when (responseCode) {
            InstallReferrerResponse.OK -> {
                val joinId = referrerClient.installReferrer.installReferrer
                Timber.d("ReferrerClient: Ok. JoinId=$joinId")
                bookRepo.setPendingJoinRequest(joinId)
                referrerClient.endConnection()
            }

            InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                Timber.w("ReferrerClient: Feature not supported")
            }

            InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                Timber.w("ReferrerClient: Service Unavailable")
            }
        }
    }

    override fun onInstallReferrerServiceDisconnected() {
        Timber.d("ReferrerClient: Disconnected")
    }
}