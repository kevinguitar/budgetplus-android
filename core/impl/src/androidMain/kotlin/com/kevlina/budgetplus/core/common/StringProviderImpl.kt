package com.kevlina.budgetplus.core.common

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

@ContributesBinding(AppScope::class)
class StringProviderImpl : StringProvider {

    override fun get(res: StringResource): String {
        return runBlocking { getString(res) }
    }

    override fun get(res: StringResource, vararg formatArgs: Any): String {
        return runBlocking { getString(res, *formatArgs) }
    }
}