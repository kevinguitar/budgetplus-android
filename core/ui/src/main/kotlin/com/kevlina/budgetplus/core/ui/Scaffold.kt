package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.material.Scaffold as MaterialScaffold

@Composable
fun Scaffold(
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    MaterialScaffold(
        bottomBar = bottomBar,
        content = content
    )
}