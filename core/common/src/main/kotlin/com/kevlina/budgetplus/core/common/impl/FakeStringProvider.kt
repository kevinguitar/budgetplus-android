package com.kevlina.budgetplus.core.common.impl

import androidx.annotation.RestrictTo
import com.kevlina.budgetplus.core.common.StringProvider

@RestrictTo(RestrictTo.Scope.TESTS)
class FakeStringProvider(
    private val stringMap: Map<Int, String> = emptyMap(),
    private val stringArrayMap: Map<Int, Array<String>> = emptyMap(),
) : StringProvider {

    override fun get(resId: Int): String = stringMap[resId]!!

    override fun get(resId: Int, vararg formatArgs: String): String = stringMap[resId]!!

    override fun getArray(resId: Int): Array<String> = stringArrayMap[resId]!!
}