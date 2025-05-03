package com.kevlina.budgetplus.core.data

import com.google.firebase.firestore.CollectionReference
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.AppStartAction
import com.kevlina.budgetplus.core.data.remote.Book
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.data.remote.UsersDb
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UserRepoImpl @Inject constructor(
    private val authManager: AuthManager,
    @UsersDb private val usersDb: Lazy<CollectionReference>,
    @AppScope private val appScope: CoroutineScope,
    private val bookRepo: BookRepo,
) : UserRepo, AppStartAction {

    private val userMapping = hashMapOf<String, User>()

    override fun onAppStart() {
        combine(
            bookRepo.bookState.filterNotNull(),
            authManager.userState.filterNotNull(),
            ::requestBookAuthors
        ).launchIn(appScope)
    }

    override fun getUser(userId: String): User? {
        val myUser = authManager.userState.value
        return if (userId == myUser?.id) {
            myUser
        } else {
            userMapping[userId]
        }
    }

    // Request the other user only once per app life, and do not request own user to save resource.
    private suspend fun requestBookAuthors(book: Book, myUser: User) {
        val authorIdsToRequest = book.authors.filter { authorId ->
            authorId != myUser.id && authorId !in userMapping
        }

        if (authorIdsToRequest.isEmpty()) return

        coroutineScope {
            authorIdsToRequest
                .map { authorId -> async { loadUser(authorId) } }
                .awaitAll()
        }
    }

    private suspend fun loadUser(userId: String) {
        try {
            val user = usersDb.get().document(userId).get().requireValue<User>()
            userMapping[userId] = user
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}