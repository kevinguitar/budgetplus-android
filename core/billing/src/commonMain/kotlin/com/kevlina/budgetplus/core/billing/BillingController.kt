package com.kevlina.budgetplus.core.billing

import kotlinx.coroutines.flow.StateFlow

interface BillingController {

    val premiumPricing: StateFlow<String?>

    val purchaseState: StateFlow<PurchaseState>

    fun buyPremium()

    fun endConnection()

}