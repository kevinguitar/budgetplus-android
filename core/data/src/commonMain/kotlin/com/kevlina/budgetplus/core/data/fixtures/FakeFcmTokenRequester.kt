package com.kevlina.budgetplus.core.data.fixtures

import androidx.annotation.RestrictTo
import com.kevlina.budgetplus.core.data.FcmTokenRequester

@RestrictTo(RestrictTo.Scope.TESTS)
class FakeFcmTokenRequester(
    var token: String? = null,
) : FcmTokenRequester {

    override suspend fun getToken(): String? = token
}
