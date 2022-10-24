package com.kevlina.budgetplus.core.data.impl

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.remote.Record
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RecordRepoImpl @Inject constructor(
    private val bookRepo: BookRepo,
) : RecordRepo {

    private val recordsDb
        get() = Firebase.firestore.collection("books")
            .document(requireNotNull(bookRepo.currentBookId) { "Book id is null" })
            .collection("records")

    override fun createRecord(record: Record) {
        recordsDb.add(record)
    }

    override fun editRecord(recordId: String, record: Record) {
        recordsDb.document(recordId).set(record)
    }

    override fun deleteRecord(recordId: String) {
        recordsDb.document(recordId).delete()
    }
}