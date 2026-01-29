package com.kevlina.budgetplus.core.data

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.CollectionReference
import com.kevlina.budgetplus.core.common.FakeStringProvider
import com.kevlina.budgetplus.core.common.FakeTracker
import com.kevlina.budgetplus.core.data.local.FakePreferenceHolder
import com.kevlina.budgetplus.core.data.remote.Book
import com.kevlina.budgetplus.core.data.remote.User
import io.mockk.mockk
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test

internal class BookRepoImplTest {

    @Test
    fun `canEdit is true when Book#allowMembersEdit is null`() = runTest {
        val repo = createRepo(book = Book(allowMembersEdit = null))
        assertThat(repo.canEdit).isTrue()
    }

    @Test
    fun `canEdit is false when Book#allowMembersEdit is false and you're not the owner`() = runTest {
        val repo = createRepo(
            book = Book(
                ownerId = "not_my_user",
                allowMembersEdit = false
            )
        )
        assertThat(repo.canEdit).isFalse()
    }

    private val tracker = FakeTracker()

    private fun TestScope.createRepo(book: Book?) = BookRepoImpl(
        authManager = FakeAuthManager(user = User(id = "my_user")),
        joinInfoProcessor = mockk(),
        stringProvider = FakeStringProvider(),
        tracker = tracker,
        preferenceHolder = FakePreferenceHolder {
            put("currentBook", Json.encodeToString(book))
        },
        appScope = backgroundScope,
        booksDb = lazy { mockk<CollectionReference>(relaxed = true) }
    )
}