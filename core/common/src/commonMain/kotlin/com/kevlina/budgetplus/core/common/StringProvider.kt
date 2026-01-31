package com.kevlina.budgetplus.core.common

import org.jetbrains.compose.resources.StringArrayResource
import org.jetbrains.compose.resources.StringResource

interface StringProvider {

    operator fun get(res: StringResource): String

    operator fun get(resId: Int): String

    operator fun get(res: StringResource, vararg formatArgs: Any): String

    fun get(resId: Int, vararg formatArgs: Any): String

    fun getArray(res: StringArrayResource): Array<String>
}