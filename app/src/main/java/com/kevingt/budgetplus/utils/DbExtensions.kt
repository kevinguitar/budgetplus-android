package com.kevingt.budgetplus.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> Task<T>.await() = suspendCoroutine<T> { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resumeWithException(it) }
}

suspend inline fun <reified T : Any> Task<DocumentSnapshot>.requireValue(): T =
    suspendCoroutine { cont ->
        addOnSuccessListener {
            val value = it.toObject<T>()
            if (value == null) {
                cont.resumeWithException(IllegalStateException("Required value is null."))
            } else {
                cont.resume(value)
            }
        }
        addOnFailureListener { cont.resumeWithException(it) }
    }