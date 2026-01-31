package com.kevlina.budgetplus.core.common

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

@ContributesBinding(AppScope::class)
class StringProviderImpl : StringProvider {

    override suspend fun get(res: StringResource): String {
        return getString(res)
    }

    override suspend fun get(res: StringResource, vararg formatArgs: Any): String {
        return getString(res, *formatArgs)
    }
}