package com.kevingt.budgetplus.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color = LocalAppColors.current.dark,
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable RowScope.() -> Unit
) = Button(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    colors = AppButtonColors(color, LocalAppColors.current.light),
    elevation = null,
    shape = shape,
    content = content
)

@Immutable
private class AppButtonColors(
    private val backgroundColor: Color,
    private val contentColor: Color
) : ButtonColors {

    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> = rememberUpdatedState(
        if (enabled) {
            backgroundColor
        } else {
            backgroundColor.copy(alpha = 0.4F)
        }
    )

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(contentColor)
}