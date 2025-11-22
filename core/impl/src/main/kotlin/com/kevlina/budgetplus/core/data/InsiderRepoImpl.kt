package com.kevlina.budgetplus.core.data

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.data.remote.UsersDb
import dagger.Lazy
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class InsiderRepoImpl @Inject constructor(
    @UsersDb private val usersDb: Lazy<CollectionReference>,
) : InsiderRepo {

    override suspend fun getTotalUsers(): Long {
        return usersDb.get()
            .count()
            .get(AggregateSource.SERVER)
            .await()
            .count
    }

    override suspend fun getTotalPremiumUsers(): Long {
        return usersDb.get()
            .whereEqualTo("premium", true)
            .count()
            .get(AggregateSource.SERVER)
            .await()
            .count
    }

    override suspend fun getTotalUsersByLanguage(language: String): Long {
        return usersDb.get()
            .whereEqualTo("language", language)
            .count()
            .get(AggregateSource.SERVER)
            .await()
            .count
    }

    override suspend fun getActiveUsers(duration: Duration): Long {
        val threshold = System.currentTimeMillis() - duration.inWholeMilliseconds
        return usersDb.get()
            .whereGreaterThanOrEqualTo("lastActiveOn", threshold)
            .count()
            .get(AggregateSource.SERVER)
            .await()
            .count
    }

    override suspend fun getNewUsers(count: Int): List<User> {
        return usersDb.get()
            .orderBy("createdOn", Query.Direction.DESCENDING)
            .limit(count.toLong())
            .get()
            .await()
            .toObjects(User::class.java)
    }

    override suspend fun getActivePremiumUsers(count: Int): List<User> {
        return usersDb.get()
            // Exclude internal users
            .whereEqualTo("internal", false)
            .whereEqualTo("premium", true)
            .orderBy("lastActiveOn", Query.Direction.DESCENDING)
            .limit(count.toLong())
            .get()
            .await()
            .toObjects(User::class.java)
    }
}