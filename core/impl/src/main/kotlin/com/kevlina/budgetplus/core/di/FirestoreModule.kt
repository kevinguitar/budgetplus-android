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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import javax.inject.Singleton

@Module
@ContributesTo(AppScope::class)
@BindingContainer
@InstallIn(SingletonComponent::class)
object FirestoreModule {

    @UsersDb
    @Singleton
    @Provides
    fun provideUsersDb(): CollectionReference {
        return Firebase.firestore.collection("users")
    }

    @BooksDb
    @Singleton
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
    @Singleton
    @Provides
    fun provideJoinInfoDb(): CollectionReference {
        return Firebase.firestore.collection("join_info")
    }

    @PurchasesDb
    @Singleton
    @Provides
    fun providePurchasesDb(): CollectionReference {
        return Firebase.firestore.collection("purchases")
    }

    @PushNotificationsDb
    @Singleton
    @Provides
    fun providePushNotificationsDb(): CollectionReference {
        return Firebase.firestore.collection("push_notifications")
    }
}