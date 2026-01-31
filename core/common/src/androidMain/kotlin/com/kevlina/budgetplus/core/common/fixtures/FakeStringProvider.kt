package com.kevlina.budgetplus.core.common.fixtures

import androidx.annotation.VisibleForTesting
import com.kevlina.budgetplus.core.common.StringProvider
import org.jetbrains.compose.resources.StringArrayResource
import org.jetbrains.compose.resources.StringResource

@VisibleForTesting
class FakeStringProvider(
    private val stringMap: Map<StringResource, String> = emptyMap(),
    private val stringIdMap: Map<Int, String> = emptyMap(),
    private val stringArrayMap: Map<StringArrayResource, Array<String>> = emptyMap(),
) : StringProvider {

    override fun get(res: StringResource): String = stringMap[res] ?: ""

    override fun get(resId: Int): String = stringIdMap[resId] ?: ""

    override fun get(res: StringResource, vararg formatArgs: Any): String = stringMap[res] ?: ""

    override fun get(resId: Int, vararg formatArgs: Any): String = stringIdMap[resId] ?: ""

    override fun getArray(res: StringArrayResource): Array<String> = stringArrayMap[res] ?: emptyArray()
}