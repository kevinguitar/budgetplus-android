package com.kevlina.budgetplus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {

    Dialog(onDismissRequest = onDismissRequest) {

        Box(
            modifier = Modifier
                .background(
                    color = LocalAppColors.current.light,
                    shape = AppTheme.dialogShape
                )
                .padding(16.dp),
            content = content
        )
    }
}