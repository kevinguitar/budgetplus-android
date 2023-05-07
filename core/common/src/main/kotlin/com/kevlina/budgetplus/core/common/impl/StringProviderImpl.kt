package com.kevlina.budgetplus.core.common.impl

import android.content.Context
import com.kevlina.budgetplus.core.common.StringProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class StringProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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