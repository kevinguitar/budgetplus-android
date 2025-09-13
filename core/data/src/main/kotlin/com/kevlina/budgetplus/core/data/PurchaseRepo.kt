package com.kevlina.budgetplus.core.data

import com.google.firebase.firestore.CollectionReference
import com.kevlina.budgetplus.core.data.remote.Purchase
import com.kevlina.budgetplus.core.data.remote.PurchasesDb
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class PurchaseRepo @Inject constructor(
    private val authManager: AuthManager,
    @PurchasesDb private val purchasesDb: dagger.Lazy<CollectionReference>,
) {
    suspend fun recordPurchase(
        orderId: String?,
        productId: String,
    ) {
        try {
            purchasesDb.get().add(Purchase(
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
        try {
            val purchases = purchasesDb.get()
                .whereEqualTo("productId", productId)
                .whereEqualTo("userId", currentUserId)
                .get()
                .await()
            return !purchases.isEmpty
        } catch (e: Exception) {
            Timber.e(e)
            return false
        }
    }
}