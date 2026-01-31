package com.kevlina.budgetplus.core.common

import org.jetbrains.compose.resources.StringResource

interface StringProvider {

    operator fun get(res: StringResource): String

    operator fun get(res: StringResource, vararg formatArgs: Any): String
}