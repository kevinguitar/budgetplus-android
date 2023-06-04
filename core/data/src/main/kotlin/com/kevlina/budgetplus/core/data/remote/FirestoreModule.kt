package com.kevlina.budgetplus.core.data.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.core.data.BookRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class UsersDb

@Qualifier
annotation class BooksDb

@Qualifier
annotation class RecordsDb

@Qualifier
annotation class JoinInfoDb

@Qualifier
annotation class PurchasesDb

@Module
@InstallIn(SingletonComponent::class)
internal object FirestoreModule {

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
}