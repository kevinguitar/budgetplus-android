package com.kevlina.budgetplus.core.common

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringProviderImpl @Inject constructor(
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