package com.kevingt.budgetplus.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun AppTheme(content: @Composable () -> Unit) {

    val customColors = CustomColors(
        light = Color(0xFFFFF3E0),
        primaryLight = Color(0xFFE0CCB1),
        primary = Color(0xFFC1A185),
        primarySemiDark = Color(0xFFB0836B),
        primaryDark = Color(0xFF907258),
        dark = Color(0xFF7E8072)
    )

    val customTypography = CustomTypography()

    CompositionLocalProvider(
        LocalAppColors provides customColors,
        LocalAppTypography provides customTypography,
        content = content
    )
}

@Immutable
data class CustomColors(
    val light: Color,
    val primaryLight: Color,
    val primary: Color,
    val primarySemiDark: Color,
    val primaryDark: Color,
    val dark: Color,
)

@Immutable
data class CustomTypography(
    val content: TextStyle = TextStyle(fontSize = 14.sp),
    val title: TextStyle = TextStyle()
)

val LocalAppColors = staticCompositionLocalOf {
    CustomColors(
        light = Color.Unspecified,
        primaryLight = Color.Unspecified,
        primary = Color.Unspecified,
        primarySemiDark = Color.Unspecified,
        primaryDark = Color.Unspecified,
        dark = Color.Unspecified,
    )
}

val LocalAppTypography = staticCompositionLocalOf {
    CustomTypography()
}