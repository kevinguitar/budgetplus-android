package com.kevlina.budgetplus.core.data

import com.google.firebase.firestore.CollectionReference
import com.kevlina.budgetplus.core.data.remote.JoinInfo
import com.kevlina.budgetplus.core.data.remote.JoinInfoDb
import java.util.UUID
import javax.inject.Inject

class JoinInfoProcessor @Inject constructor(
    @JoinInfoDb private val joinInfoDb: dagger.Lazy<CollectionReference>,
) {

    fun generateJoinId(bookId: String): String {
        val joinId = UUID.randomUUID().toString()
        joinInfoDb.get().document(joinId).set(
            JoinInfo(
                bookId = bookId,
                generatedOn = System.currentTimeMillis()
            )
        )
        return joinId
    }

    suspend fun resolveJoinId(joinId: String): JoinInfo? {
        // joinId could be the random referral from GP, ignore these cases.
        if (joinId.startsWith("utm_source") || joinId.startsWith("gclid")) {
            return null
        }

        return try {
            joinInfoDb.get().document(joinId).get().requireValue()
        } catch (e: Exception) {
            e.toString()
            throw JoinBookException.JoinInfoNotFound("Couldn't resolve the join id. $joinId")
        }
    }
}