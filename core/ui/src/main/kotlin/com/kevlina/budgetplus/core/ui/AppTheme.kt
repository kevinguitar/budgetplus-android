package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppTheme(content: @Composable () -> Unit) {

    val customColors = CustomColors(
        light = Color(0xFFFFF3E0),
        lightBg = Color(0x33C1A185),
        primaryLight = Color(0xFFE0CCB1),
        primary = Color(0xFFC1A185),
        primarySemiDark = Color(0xFFB0836B),
        primaryDark = Color(0xFF907258),
        dark = Color(0xFF7E8072)
    )

    CompositionLocalProvider(
        LocalAppColors provides customColors,
        content = content
    )
}

@Immutable
data class CustomColors(
    val light: Color,
    val lightBg: Color,
    val primaryLight: Color,
    val primary: Color,
    val primarySemiDark: Color,
    val primaryDark: Color,
    val dark: Color,
)

val LocalAppColors = staticCompositionLocalOf {
    CustomColors(
        light = Color.Unspecified,
        lightBg = Color.Unspecified,
        primaryLight = Color.Unspecified,
        primary = Color.Unspecified,
        primarySemiDark = Color.Unspecified,
        primaryDark = Color.Unspecified,
        dark = Color.Unspecified,
    )
}

object AppTheme {

    val maxContentWidth: Dp get() = 568.dp
    val twoPanelMinWidth: Dp get() = 600.dp
    val packedMaxHeight: Dp get() = 480.dp
    val dialogShape: Shape get() = RoundedCornerShape(12.dp)
    val cardShape: Shape get() = RoundedCornerShape(12.dp)

}