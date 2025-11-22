package com.kevlina.budgetplus.core.data

import com.google.firebase.firestore.CollectionReference
import com.kevlina.budgetplus.core.data.remote.Purchase
import com.kevlina.budgetplus.core.data.remote.PurchasesDb
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class PurchaseRepo @Inject constructor(
    private val authManager: AuthManager,
    @PurchasesDb private val purchasesDb: Lazy<CollectionReference>,
) {
    suspend fun recordPurchase(
        orderId: String?,
        productId: String,
    ) {
        try {
            purchasesDb.value.add(Purchase(
                orderId = orderId,
                productId = productId,
                userId = authManager.userId,
                purchasedOn = System.currentTimeMillis()
            )).await()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    suspend fun hasPurchaseBelongsToCurrentUser(
        productId: String,
    ): Boolean {
        val currentUserId = authManager.userId ?: return false
        return try {
            val purchases = purchasesDb.value
                .whereEqualTo("productId", productId)
                .whereEqualTo("userId", currentUserId)
                .get()
                .await()
            !purchases.isEmpty
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }
}