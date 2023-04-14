package com.kevlina.budgetplus.feature.unlock.premium

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.billing.BillingController
import com.kevlina.budgetplus.core.billing.PurchaseState
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val billingController: BillingController,
    private val toaster: Toaster,
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
                    toaster.showMessage(R.string.premium_acknowledge_fail)
                    return@map true
                }

                PurchaseState.Success -> {
                    tracker.logEvent("buy_premium_success")
                    toaster.showMessage(R.string.premium_unlocked)
                    return@map true
                }

                is PurchaseState.Fail -> {
                    tracker.logEvent("buy_premium_fail")
                    toaster.showMessage(state.error)
                }

                PurchaseState.Canceled -> tracker.logEvent("buy_premium_cancel")
                else -> Unit
            }
            false
        }

    fun getPremium(context: Context) {
        tracker.logEvent("buy_premium_attempt")
        billingController.buyPremium(context as ComponentActivity)
    }

    override fun onCleared() {
        billingController.endConnection()
    }
}