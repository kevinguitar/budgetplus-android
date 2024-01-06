package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors

@Composable
fun AppTheme(
    themeColors: ThemeColors = ThemeColors.MilkTea,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAppColors provides themeColors,
        content = content
    )
}

object AppTheme {

    val maxContentWidth: Dp get() = 568.dp
    val dialogShape: Shape get() = RoundedCornerShape(cornerRadius)
    val cardShape: Shape get() = RoundedCornerShape(cornerRadius)
    val cornerRadius: Dp get() = 12.dp

    const val DIVIDER_ALPHA: Float = 0.4F

}