package com.kevlina.budgetplus.core.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend inline fun <reified T : Any> Task<DocumentSnapshot>.requireValue(): T =
    suspendCoroutine { cont ->
        addOnSuccessListener {
            val value = it.toObject<T>()
            if (value == null) {
                cont.resumeWithException(DocNotExistsException())
            } else {
                cont.resume(value)
            }
        }
        addOnFailureListener { cont.resumeWithException(it) }
    }

class DocNotExistsException : IllegalStateException("Document doesn't exists")