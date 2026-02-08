package com.kevlina.budgetplus.book.di

import co.touchlab.kermit.Logger
import com.kevlina.budgetplus.core.ads.AdMobInitializer
import com.kevlina.budgetplus.core.ads.FullScreenAdsLoader
import com.kevlina.budgetplus.core.billing.BillingController
import com.kevlina.budgetplus.core.billing.PurchaseState
import com.kevlina.budgetplus.core.common.ShareHelper
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.feature.settings.SettingsNavigation
import com.kevlina.budgetplus.feature.speak.record.RecordActor
import com.kevlina.budgetplus.feature.speak.record.SpeakToRecord
import com.kevlina.budgetplus.inapp.review.InAppReviewManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource

@ContributesBinding(AppScope::class)
class AdMobInitializerImpl : AdMobInitializer {
    override val isInitialized: StateFlow<Boolean>
        get() = MutableStateFlow(true)

    override fun initialize() {
    }

    override suspend fun ensureInitialized() {

    }
}

@ContributesBinding(AppScope::class)
class ToasterImpl : Toaster {
    override fun showMessage(message: CharSequence) {
        Logger.d { "Showing toast: $message" }
    }
}

@ContributesBinding(AppScope::class)
class SettingsNavigationImpl : SettingsNavigation {
    override fun openLanguageSettings() {
        TODO("Not yet implemented")
    }

    override suspend fun contactUs() {
        TODO("Not yet implemented")
    }

    override fun visitUrl(url: String) {
        TODO("Not yet implemented")
    }
}

@ContributesBinding(AppScope::class)
class FullScreenAdsLoaderImpl : FullScreenAdsLoader {
    override fun showAd() {
        TODO("Not yet implemented")
    }
}

@ContributesBinding(AppScope::class)
class InAppReviewManagerImpl : InAppReviewManager {
    override suspend fun isEligibleForReview(): Boolean = false

    override suspend fun launchReviewFlow() {
        TODO("Not yet implemented")
    }

    override fun rejectReviewing() {
        TODO("Not yet implemented")
    }
}

@ContributesBinding(AppScope::class)
class SpeakToRecordImpl : SpeakToRecord {
    override val isAvailableOnDevice: Boolean
        get() = false

    override fun startRecording(): RecordActor {
        TODO("Not yet implemented")
    }
}

@ContributesBinding(AppScope::class)
class BillingControllerImpl : BillingController {
    override val premiumPricing: StateFlow<String?>
        get() = TODO("Not yet implemented")
    override val purchaseState: StateFlow<PurchaseState>
        get() = TODO("Not yet implemented")

    override fun buyPremium() {
        TODO("Not yet implemented")
    }

    override fun endConnection() {
        TODO("Not yet implemented")
    }
}

@ContributesBinding(AppScope::class)
class ShareHelperImpl: ShareHelper {
    override suspend fun share(title: StringResource, text: String) {
        TODO("Not yet implemented")
    }
}