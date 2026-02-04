package com.kevlina.budgetplus.core.adaptive

import androidx.compose.runtime.Composable

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

data class WindowSizeClass(
    val width: Size,
    val height: Size,
) {

    enum class Size {
        Compat, Medium, Expanded
    }

    companion object {
        @Composable
        fun calculate(): WindowSizeClass {
            val containerSize = LocalWindowInfo.current.containerSize
            val density = LocalDensity.current

            val widthDp = with(density) { containerSize.width.toDp() }
            val heightDp = with(density) { containerSize.height.toDp() }

            return WindowSizeClass(
                width = when {
                    widthDp < 600.dp -> Size.Compat
                    widthDp < 840.dp -> Size.Medium
                    else -> Size.Expanded
                },
                height = when {
                    heightDp < 480.dp -> Size.Compat
                    heightDp < 900.dp -> Size.Medium
                    else -> Size.Expanded
                }
            )
        }
    }
}