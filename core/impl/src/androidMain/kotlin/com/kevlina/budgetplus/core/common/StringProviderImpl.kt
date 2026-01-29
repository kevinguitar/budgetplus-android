package com.kevlina.budgetplus.core.common

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class StringProviderImpl(
    private val context: Context,
) : StringProvider {

    override fun get(resId: Int): String {
        return context.getString(resId)
    }

    override fun get(resId: Int, vararg formatArgs: String): String {
        return context.getString(resId, *formatArgs)
    }

    override fun getArray(resId: Int): Array<String> {
        return context.resources.getStringArray(resId)
    }
}