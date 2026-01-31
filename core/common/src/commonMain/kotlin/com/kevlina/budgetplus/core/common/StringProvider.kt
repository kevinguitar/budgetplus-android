package com.kevlina.budgetplus.core.common

import org.jetbrains.compose.resources.StringResource

interface StringProvider {

    operator suspend fun get(res: StringResource): String

    operator suspend fun get(res: StringResource, vararg formatArgs: Any): String
}