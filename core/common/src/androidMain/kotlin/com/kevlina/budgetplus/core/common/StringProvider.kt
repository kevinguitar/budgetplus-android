package com.kevlina.budgetplus.core.common

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

interface StringProvider {

    operator fun get(@StringRes resId: Int): String

    operator fun get(@StringRes resId: Int, vararg formatArgs: String): String

    fun getArray(@ArrayRes resId: Int): Array<String>
}