package com.kevlina.budgetplus.core.common

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringArrayResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getStringArray

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class StringProviderImpl(
    private val context: Context,
) : StringProvider {

    override fun get(res: StringResource): String {
        //TODO: what should I do?
        return runBlocking { getString(res) }
    }

    override fun get(resId: Int): String {
        return context.getString(resId)
    }

    override fun get(res: StringResource, vararg formatArgs: Any): String {
        return runBlocking { getString(res, *formatArgs) }
    }

    override fun get(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }

    override fun getArray(res: StringArrayResource): Array<String> {
        return runBlocking { getStringArray(res).toTypedArray() }
    }
}