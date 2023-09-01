package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.ColorTone
import com.kevlina.budgetplus.core.theme.LocalAppColors

@Composable
fun AppTheme(
    colorTone: ColorTone = ColorTone.MilkTea,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAppColors provides colorTone.themeColors,
        content = content
    )
}

object AppTheme {

    val maxContentWidth: Dp get() = 568.dp
    val dialogShape: Shape get() = RoundedCornerShape(12.dp)
    val cardShape: Shape get() = RoundedCornerShape(12.dp)

    const val DIVIDER_ALPHA: Float = 0.4F

}