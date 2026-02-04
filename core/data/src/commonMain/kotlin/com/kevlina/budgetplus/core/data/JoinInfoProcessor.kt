package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.common.randomUUID
import com.kevlina.budgetplus.core.data.remote.JoinInfo
import com.kevlina.budgetplus.core.data.remote.JoinInfoDb
import dev.gitlive.firebase.firestore.CollectionReference
import dev.zacsweers.metro.Inject
import kotlin.time.Clock

@Inject
class JoinInfoProcessor(
    @JoinInfoDb private val joinInfoDb: Lazy<CollectionReference>,
) {

    suspend fun generateJoinId(bookId: String): String {
        val joinId = randomUUID()
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
            joinInfoDb.value.document(joinId).get().data()
        } catch (e: Exception) {
            e.toString()
            throw JoinBookException.JoinInfoNotFound("Couldn't resolve the join id. $joinId")
        }
    }
}