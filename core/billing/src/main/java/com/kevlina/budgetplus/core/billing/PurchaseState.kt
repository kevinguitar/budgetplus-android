package com.kevlina.budgetplus.core.billing

sealed class PurchaseState {

    object Inactive : PurchaseState()

    object PaymentProcessing : PurchaseState()

    object PaymentAcknowledgeFailed : PurchaseState()

    object Success : PurchaseState()

    object Canceled : PurchaseState()

    data class Fail(val error: String) : PurchaseState()
}
