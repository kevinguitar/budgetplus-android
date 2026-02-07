package com.kevlina.budgetplus.core.common

import org.jetbrains.compose.resources.StringResource

interface ShareHelper {
    suspend fun share(
        title: StringResource,
        text: String,
    )
}