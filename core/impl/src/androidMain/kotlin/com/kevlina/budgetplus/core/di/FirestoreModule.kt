package com.kevlina.budgetplus.core.di

import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.remote.BooksDb
import com.kevlina.budgetplus.core.data.remote.JoinInfoDb
import com.kevlina.budgetplus.core.data.remote.PurchasesDb
import com.kevlina.budgetplus.core.data.remote.PushNotificationsDb
import com.kevlina.budgetplus.core.data.remote.RecordsDb
import com.kevlina.budgetplus.core.data.remote.UsersDb
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface FirestoreModule {

    @UsersDb
    @SingleIn(AppScope::class)
    @Provides
    fun provideUsersDb(): CollectionReference {
        return Firebase.firestore.collection("users")
    }

    @BooksDb
    @SingleIn(AppScope::class)
    @Provides
    fun provideBooksDb(): CollectionReference {
        return Firebase.firestore.collection("books")
    }

    @RecordsDb
    @Provides
    fun provideRecordsDb(bookRepo: BookRepo): CollectionReference {
        return Firebase.firestore.collection("books")
            .document(requireNotNull(bookRepo.currentBookId) { "Book id is null" })
            .collection("records")
    }

    @JoinInfoDb
    @SingleIn(AppScope::class)
    @Provides
    fun provideJoinInfoDb(): CollectionReference {
        return Firebase.firestore.collection("join_info")
    }

    @PurchasesDb
    @SingleIn(AppScope::class)
    @Provides
    fun providePurchasesDb(): CollectionReference {
        return Firebase.firestore.collection("purchases")
    }

    @PushNotificationsDb
    @SingleIn(AppScope::class)
    @Provides
    fun providePushNotificationsDb(): CollectionReference {
        return Firebase.firestore.collection("push_notifications")
    }
}