package com.kevlina.budgetplus.billing

import android.content.Context
import androidx.activity.ComponentActivity
import com.android.billingclient.api.*
import com.kevlina.budgetplus.auth.AuthManager
import com.kevlina.budgetplus.utils.AppScope
import com.kevlina.budgetplus.utils.mapState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class BillingControllerImpl @Inject constructor(
    @ApplicationContext context: Context,
    @AppScope private val appScope: CoroutineScope,
    private val authManager: AuthManager,
) : BillingController, PurchasesUpdatedListener, BillingClientStateListener {

    private val _premiumProduct = MutableStateFlow<ProductDetails?>(null)
    override val premiumPricing: StateFlow<String?> = _premiumProduct
        .mapState { it?.oneTimePurchaseOfferDetails?.formattedPrice }

    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Inactive)
    override val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    init {
        if (!billingClient.isReady) {
            Timber.d("BillingClient: Start connection")
            billingClient.startConnection(this)
        }
    }

    override fun endConnection() {
        if (billingClient.isReady) {
            Timber.d("BillingClient: End connection")
            billingClient.endConnection()
        }
    }

    override fun onBillingServiceDisconnected() {
        Timber.d("BillingClient: onBillingServiceDisconnected")
    }

    override fun buyPremium(activity: ComponentActivity) {
        val productDetails = requireNotNull(_premiumProduct.value)
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build()

        val billingParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()

        billingClient.launchBillingFlow(activity, billingParams)
    }

    /**
     * Called by the Billing Library when new purchases are detected.
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?,
    ) {
        val status = BillingStatus(billingResult.responseCode)
        val debugMessage = billingResult.debugMessage
        Timber.d("BillingClient: onPurchasesUpdated $status $debugMessage")

        if (status != BillingStatus.OK) {
            _purchaseState.value = if (status == BillingStatus.USER_CANCELED) {
                PurchaseState.Canceled
            } else {
                PurchaseState.Fail(status.toString())
            }
            return
        }

        _purchaseState.value = PurchaseState.PaymentProcessing

        appScope.launch {
            processPurchases(purchases.orEmpty())
        }
    }


    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val status = BillingStatus(billingResult.responseCode)
        val debugMessage = billingResult.debugMessage
        Timber.d("BillingClient: onBillingSetupFinished $status $debugMessage")

        if (status == BillingStatus.OK) {
            queryProductDetails()
            queryPurchases()
        }
    }

    private fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        appScope.launch {
            val (billingResult, purchases) = billingClient.queryPurchasesAsync(params)
            val status = BillingStatus(billingResult.responseCode)
            val debugMessage = billingResult.debugMessage

            if (status == BillingStatus.OK) {
                Timber.d("BillingClient: queryPurchases success ${purchases.size}")
                processPurchases(purchases)
            } else {
                Timber.e("BillingClient: queryPurchases error $status $debugMessage")
            }
        }
    }

    private fun queryProductDetails() {
        val premium = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(PRODUCT_PREMIUM_ID)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(premium))
            .build()

        appScope.launch {
            val (billingResult, products) = billingClient.queryProductDetails(params)
            val status = BillingStatus(billingResult.responseCode)
            val debugMessage = billingResult.debugMessage

            if (status != BillingStatus.OK) {
                Timber.e("BillingClient: ProductDetailsResponse $status $debugMessage")
                return@launch
            }

            val premiumProduct = products?.firstOrNull()
            if (premiumProduct == null) {
                Timber.w("BillingClient: ProductDetailsResponse is empty.")
            } else {
                Timber.d("BillingClient: ProductDetailsResponse offer $premiumProduct")
                _premiumProduct.value = premiumProduct
            }
        }
    }

    private val acknowledgeMaxAttempt = 3

    private suspend fun processPurchases(purchases: List<Purchase>) {
        purchases.forEach { purchase ->
            if (purchase.isAcknowledged) {
                Timber.d("BillingClient: Found purchased product ${purchase.products.joinToString()}")
            } else {
                // Acknowledge the purchase
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                repeat(acknowledgeMaxAttempt) { attempt ->
                    Timber.d("BillingClient: Acknowledge attempt ${attempt + 1}")
                    val billingResult = billingClient.acknowledgePurchase(params)
                    val status = BillingStatus(billingResult.responseCode)

                    if (status == BillingStatus.OK) {
                        if (PRODUCT_PREMIUM_ID in purchase.products) {
                            // Code snippet to consume the product, useful for testing purposes.
                            /*billingClient.consumePurchase(
                                ConsumeParams.newBuilder()
                                    .setPurchaseToken(purchases.first().purchaseToken)
                                    .build()
                            )*/

                            authManager.markPremium()
                            _purchaseState.value = PurchaseState.Success
                        }
                        return@repeat
                    }

                    if (attempt == acknowledgeMaxAttempt - 1) {
                        Timber.e("BillingClient: Acknowledge failed ${billingResult.debugMessage}")
                        _purchaseState.value = PurchaseState.PaymentAcknowledgeFailed
                    }
                }
            }
        }
    }
}