package com.kevlina.budgetplus.data.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.utils.requireValue
import java.util.UUID
import javax.inject.Inject

class JoinInfoProcessor @Inject constructor() {

    private val joinInfoDb by lazy {
        Firebase.firestore.collection("join_info")
    }

    fun generateJoinId(bookId: String): String {
        val joinId = UUID.randomUUID().toString()
        joinInfoDb.document(joinId).set(
            JoinInfo(
                bookId = bookId,
                generatedOn = System.currentTimeMillis()
            )
        )
        return joinId
    }

    suspend fun resolveJoinId(joinId: String): JoinInfo {
        return joinInfoDb.document(joinId).get().requireValue()
    }
}