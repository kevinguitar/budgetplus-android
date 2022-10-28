package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.User

interface UserRepo {

    fun getUser(userId: String): User?

}