package com.kevlina.budgetplus.core.data

import com.google.firebase.firestore.CollectionReference
import com.kevlina.budgetplus.core.data.remote.JoinInfo
import com.kevlina.budgetplus.core.data.remote.JoinInfoDb
import dev.zacsweers.metro.Inject
import java.util.UUID
import kotlin.time.Clock

@Inject
class JoinInfoProcessor(
    @JoinInfoDb private val joinInfoDb: Lazy<CollectionReference>,
) {

    fun generateJoinId(bookId: String): String {
        val joinId = UUID.randomUUID().toString()
        joinInfoDb.value.document(joinId).set(
            JoinInfo(
                bookId = bookId,
                generatedOn = Clock.System.now().toEpochMilliseconds()
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
            joinInfoDb.value.document(joinId).get().requireValue()
        } catch (e: Exception) {
            e.toString()
            throw JoinBookException.JoinInfoNotFound("Couldn't resolve the join id. $joinId")
        }
    }
}