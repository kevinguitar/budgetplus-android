package com.kevlina.budgetplus.core.billing

sealed class PurchaseState {

    data object Inactive : PurchaseState()

    data object PaymentProcessing : PurchaseState()

    data object PaymentAcknowledgeFailed : PurchaseState()

    data object Success : PurchaseState()

    data object Canceled : PurchaseState()

    data class Fail(val error: String) : PurchaseState()
}