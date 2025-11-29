package com.kevlina.budgetplus.core.ads

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.kevlina.budgetplus.core.common.AppCoroutineScope
import com.kevlina.budgetplus.core.data.AuthManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@SingleIn(AppScope::class)
@Inject
class AdMobInitializer(
    private val context: Context,
    @AppCoroutineScope appScope: CoroutineScope,
    private val authManager: AuthManager,
) {

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized = _isInitialized.asStateFlow()

    init {
        // Handle the case when re-login with a non-premium user.
        authManager.userState
            .drop(1)
            .onEach { initialize() }
            .launchIn(appScope)
    }

    fun initialize() {
        if (authManager.isPremium.value || isInitialized.value) {
            return
        }

        MobileAds.initialize(context) {
            _isInitialized.value = true
            Timber.d("AdMob initialized: ${
                it.adapterStatusMap
                    .map { (key, value) -> "$key: ${value.description}" }
                    .joinToString("\n")
            }")
        }
    }

    suspend fun ensureInitialized() {
        isInitialized.filter { it }.first()
    }
}