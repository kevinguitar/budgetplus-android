package com.kevlina.budgetplus.core.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//TODO: Replace it with the kotlin one
suspend fun <T> Task<T>.await() = suspendCoroutine<T> { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resumeWithException(it) }
}

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