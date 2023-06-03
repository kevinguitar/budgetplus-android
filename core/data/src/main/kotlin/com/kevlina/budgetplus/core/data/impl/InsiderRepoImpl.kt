package com.kevlina.budgetplus.core.data.impl

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.kevlina.budgetplus.core.data.InsiderRepo
import com.kevlina.budgetplus.core.data.await
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.data.remote.UsersDb
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class InsiderRepoImpl @Inject constructor(
    @UsersDb private val usersDb: dagger.Lazy<CollectionReference>,
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

    override suspend fun getDailyActiveUsers(): Long {
        val threshold = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
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
}