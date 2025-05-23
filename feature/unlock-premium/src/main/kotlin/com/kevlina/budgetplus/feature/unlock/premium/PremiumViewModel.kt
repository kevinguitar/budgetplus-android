package com.kevlina.budgetplus.feature.unlock.premium

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.billing.BillingController
import com.kevlina.budgetplus.core.billing.PurchaseState
import com.kevlina.budgetplus.core.common.ActivityProvider
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.bundle
import com.kevlina.budgetplus.core.data.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val billingController: BillingController,
    private val activityProvider: ActivityProvider,
    private val snackbarSender: SnackbarSender,
    private val tracker: Tracker,
    authManager: AuthManager,
) : ViewModel() {

    val isPremium = authManager.isPremium

    val premiumPricing = billingController.premiumPricing

    val isPaymentProcessing = billingController.purchaseState
        .map { it is PurchaseState.PaymentProcessing }

    val purchaseDoneFlow: Flow<Boolean> = billingController.purchaseState
        .map { state ->
            when (state) {
                PurchaseState.PaymentAcknowledgeFailed -> {
                    snackbarSender.send(R.string.premium_acknowledge_fail)
                    return@map true
                }

                PurchaseState.Success -> {
                    tracker.logEvent("buy_premium_success")
                    snackbarSender.send(R.string.premium_unlocked)
                    return@map true
                }

                is PurchaseState.Fail -> {
                    tracker.logEvent(
                        event = "buy_premium_fail",
                        params = bundle { putString("reason", state.error) }
                    )
                    snackbarSender.send(state.error)
                }

                PurchaseState.Canceled -> tracker.logEvent("buy_premium_cancel")
                else -> Unit
            }
            false
        }

    fun getPremium() {
        val activity = activityProvider.currentActivity ?: return
        tracker.logEvent("buy_premium_attempt")
        billingController.buyPremium(activity)
    }

    override fun onCleared() {
        billingController.endConnection()
    }
}