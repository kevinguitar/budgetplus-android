package com.kevlina.budgetplus.billing

import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.StateFlow

interface BillingController {

    val premiumPricing: StateFlow<String?>

    val purchaseState: StateFlow<PurchaseState>

    fun buyPremium(activity: ComponentActivity)

    fun endConnection()

}