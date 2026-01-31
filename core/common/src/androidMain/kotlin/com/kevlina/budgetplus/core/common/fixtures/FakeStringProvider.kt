package com.kevlina.budgetplus.core.common.fixtures

import androidx.annotation.VisibleForTesting
import com.kevlina.budgetplus.core.common.StringProvider
import org.jetbrains.compose.resources.StringResource

@VisibleForTesting
class FakeStringProvider(
    private val stringMap: Map<StringResource, String> = emptyMap(),
) : StringProvider {

    override suspend fun get(res: StringResource): String = stringMap[res].orEmpty()

    override suspend fun get(res: StringResource, vararg formatArgs: Any): String = stringMap[res].orEmpty()
}