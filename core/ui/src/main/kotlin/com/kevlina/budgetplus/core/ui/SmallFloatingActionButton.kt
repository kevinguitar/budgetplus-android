package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kevlina.budgetplus.core.theme.LocalAppColors
import androidx.compose.material3.SmallFloatingActionButton as MaterialSmallFloatingActionButton

@Composable
fun SmallFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {

    MaterialSmallFloatingActionButton(
        modifier = modifier,
        shape = CircleShape,
        containerColor = LocalAppColors.current.dark,
        content = content,
        onClick = onClick
    )
}