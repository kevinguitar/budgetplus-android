package com.kevingt.moneybook.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.BookRepoImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
annotation class BooksDb

@Module
@InstallIn(SingletonComponent::class)
interface DbModule {

    @Binds
    fun provideBookRepo(impl: BookRepoImpl): BookRepo

    companion object {

        @Provides
        @BooksDb
        fun provideBooksDb(): CollectionReference {
            return Firebase.firestore.collection("books")
        }
    }
}