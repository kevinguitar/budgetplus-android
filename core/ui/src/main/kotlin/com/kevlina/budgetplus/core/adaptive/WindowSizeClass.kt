package com.kevlina.budgetplus.core.adaptive

import android.app.Activity
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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
            val activity = LocalContext.current as Activity
            val materialClass = calculateWindowSizeClass(activity = activity)

            return WindowSizeClass(
                width = when (materialClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> Size.Compat
                    WindowWidthSizeClass.Medium -> Size.Medium
                    WindowWidthSizeClass.Expanded -> Size.Expanded
                    else -> error("Not supported width ${materialClass.widthSizeClass}")
                },
                height = when (materialClass.heightSizeClass) {
                    WindowHeightSizeClass.Compact -> Size.Compat
                    WindowHeightSizeClass.Medium -> Size.Medium
                    WindowHeightSizeClass.Expanded -> Size.Expanded
                    else -> error("Not supported height ${materialClass.heightSizeClass}")
                }
            )
        }
    }
}