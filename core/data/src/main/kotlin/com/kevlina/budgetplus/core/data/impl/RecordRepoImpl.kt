package com.kevlina.budgetplus.core.data.impl

import com.google.firebase.firestore.CollectionReference
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.RecordsDb
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
internal class RecordRepoImpl @Inject constructor(
    @RecordsDb private val recordsDb: Provider<CollectionReference>,
) : RecordRepo {

    override fun createRecord(record: Record) {
        recordsDb.get().add(record)
    }

    override fun editRecord(recordId: String, record: Record) {
        recordsDb.get().document(recordId).set(record)
    }

    override fun deleteRecord(recordId: String) {
        recordsDb.get().document(recordId).delete()
    }
}