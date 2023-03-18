package com.kevlina.budgetplus.core.ui

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector

private const val DISABLED_ALPHA = 0.5F

@Composable
fun MenuAction(
    imageVector: ImageVector,
    description: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = description,
            tint = LocalAppColors.current.light,
            modifier = Modifier.thenIf(!enabled) {
                Modifier.alpha(DISABLED_ALPHA)
            }
        )
    }
}