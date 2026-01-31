package com.kevlina.budgetplus.core.common.fixtures

import androidx.annotation.VisibleForTesting
import com.kevlina.budgetplus.core.common.StringProvider
import org.jetbrains.compose.resources.StringResource

@VisibleForTesting
class FakeStringProvider(
    private val stringMap: Map<StringResource, String> = emptyMap(),
) : StringProvider {

    override fun get(res: StringResource): String = stringMap[res].orEmpty()

    override fun get(res: StringResource, vararg formatArgs: Any): String = stringMap[res].orEmpty()
}