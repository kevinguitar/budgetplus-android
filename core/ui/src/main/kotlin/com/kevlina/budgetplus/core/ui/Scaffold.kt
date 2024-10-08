package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold as MaterialScaffold

@Composable
fun Scaffold(
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    MaterialScaffold(
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        content = content
    )
}