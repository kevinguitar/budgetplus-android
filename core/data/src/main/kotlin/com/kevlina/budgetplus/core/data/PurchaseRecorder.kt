package com.kevlina.budgetplus.core.data

import com.google.firebase.firestore.CollectionReference
import com.kevlina.budgetplus.core.data.remote.Purchase
import com.kevlina.budgetplus.core.data.remote.PurchasesDb
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class PurchaseRecorder @Inject constructor(
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
                userId = authManager.userState.value?.id,
                purchasedOn = System.currentTimeMillis()
            )).await()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}