package com.kevlina.budgetplus.core.ads

import android.content.Context
import co.touchlab.kermit.Logger
import com.google.android.gms.ads.MobileAds
import com.kevlina.budgetplus.core.common.AppCoroutineScope
import com.kevlina.budgetplus.core.data.AuthManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AdMobInitializerImpl(
    private val context: Context,
    @AppCoroutineScope appScope: CoroutineScope,
    private val authManager: AuthManager,
) : AdMobInitializer {

    final override val isInitialized: StateFlow<Boolean>
        field = MutableStateFlow(false)

    init {
        // Handle the case when re-login with a non-premium user.
        authManager.userState
            .drop(1)
            .onEach { initialize() }
            .launchIn(appScope)
    }

    override fun initialize() {
        if (authManager.isPremium.value || isInitialized.value) {
            return
        }

        MobileAds.initialize(context) {
            isInitialized.value = true
            Logger.d {
                "AdMob initialized: ${
                    it.adapterStatusMap
                        .map { (key, value) -> "$key: ${value.description}" }
                        .joinToString("\n")
                }"
            }
        }
    }

    override suspend fun ensureInitialized() {
        isInitialized.filter { it }.first()
    }
}