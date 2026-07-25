package com.kevlina.budgetplus.core.data.fixtures

import androidx.annotation.RestrictTo
import com.kevlina.budgetplus.core.data.UserRepo
import com.kevlina.budgetplus.core.data.remote.User

@RestrictTo(RestrictTo.Scope.TESTS)
class FakeUserRepo(
    private val users: Map<String, User> = emptyMap(),
) : UserRepo {

    override fun getUser(userId: String): User? = users[userId]
}
