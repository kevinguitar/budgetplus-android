package com.kevlina.budgetplus.core.billing

import android.content.Context
import androidx.activity.ComponentActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.kevlina.budgetplus.core.common.AppCoroutineScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.PurchaseRepo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@ContributesBinding(AppScope::class, binding = binding<BillingController>())
class BillingControllerImpl(
    context: Context,
    @AppCoroutineScope private val appScope: CoroutineScope,
    private val authManager: AuthManager,
    private val purchaseRepo: PurchaseRepo,
    private val stringProvider: StringProvider,
) : BillingController, PurchasesUpdatedListener, BillingClientStateListener {

    private val productDetailsState = MutableStateFlow<ProductDetails?>(null)
    override val premiumPricing: StateFlow<String?> = productDetailsState
        .mapState { it?.oneTimePurchaseOfferDetails?.formattedPrice }

    final override val purchaseState: StateFlow<PurchaseState>
        field = MutableStateFlow<PurchaseState>(PurchaseState.Inactive)

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .enableAutoServiceReconnection()
        .build()

    init {
        Timber.d("BillingClient: Start connection")
        billingClient.startConnection(this)
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
        val productDetails = requireNotNull(productDetailsState.value)
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

        when (status) {
            BillingStatus.OK -> {
                purchaseState.value = PurchaseState.PaymentProcessing
                appScope.launch {
                    processPurchases(purchases.orEmpty())
                }
            }

            BillingStatus.USER_CANCELED -> {
                purchaseState.value = PurchaseState.Canceled
            }

            // Item already owned, it's likely due to the loss of premium status, try to recover it.
            BillingStatus.ITEM_ALREADY_OWNED -> appScope.launch {
                purchaseState.value =
                    if (purchaseRepo.hasPurchaseBelongsToCurrentUser(PRODUCT_PREMIUM_ID)) {
                        authManager.markPremium()
                        Timber.e(BillingRestoredException("Premium is restored for ${authManager.userId}"))
                        PurchaseState.Success
                    } else {
                        PurchaseState.Fail(status.toString())
                    }
            }

            else -> {
                Timber.e(BillingException("onPurchasesUpdated $status $debugMessage. UserId=${authManager.userId}"))
                purchaseState.value = PurchaseState.Fail(status.toString())
            }
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
                Timber.e(BillingException("queryPurchases error $status $debugMessage"))
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
                Timber.e(BillingException("ProductDetailsResponse $status $debugMessage"))
                return@launch
            }

            val premiumProduct = products?.firstOrNull()
            if (premiumProduct == null) {
                Timber.w("BillingClient: ProductDetailsResponse is empty.")
            } else {
                Timber.d("BillingClient: ProductDetailsResponse offer $premiumProduct")
                productDetailsState.value = premiumProduct
            }
        }
    }

    private suspend fun processPurchases(purchases: List<Purchase>) {
        purchases.forEach { purchase ->
            when {
                purchase.isAcknowledged -> {
                    Timber.d("BillingClient: Found purchased product ${purchase.products.joinToString()}")
                }

                purchase.purchaseState == Purchase.PurchaseState.PURCHASED -> {
                    // Acknowledge the purchase
                    val params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                    Timber.d("BillingClient: Acknowledging the purchase.")
                    val billingResult = billingClient.acknowledgePurchase(params)
                    val status = BillingStatus(billingResult.responseCode)

                    if (status == BillingStatus.OK && PRODUCT_PREMIUM_ID in purchase.products) {
                        authManager.markPremium()
                        purchaseRepo.recordPurchase(purchase.orderId, purchase.products.first())
                        purchaseState.value = PurchaseState.Success
                    } else {
                        Timber.e(BillingException("Acknowledge failed ${billingResult.debugMessage}"))
                        purchaseState.value = PurchaseState.PaymentAcknowledgeFailed
                    }
                }

                else -> {
                    Timber.e(BillingException("Cannot process the purchase. Purchase state: ${purchase.purchaseState}"))
                    purchaseState.value = PurchaseState.Fail(stringProvider[R.string.premium_payment_pending])
                }
            }
        }
    }

    private companion object {
        const val PRODUCT_PREMIUM_ID: String = "budgetplus.premium"
    }
}
