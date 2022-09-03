package com.kevlina.budgetplus.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AdaptiveScreen(
    modifier: Modifier,
    regularContent: @Composable () -> Unit,
    wideContent: @Composable () -> Unit,
    packedContent: @Composable () -> Unit,
    extraContent: @Composable (BoxScope.() -> Unit)? = null
) {

    BoxWithConstraints(modifier = modifier) {

        when {
            maxWidth > AppTheme.twoPanelMinWidth && maxWidth > maxHeight -> wideContent()
            maxHeight < AppTheme.packedMaxHeight -> packedContent()
            else -> regularContent()
        }

        if (extraContent != null) {
            extraContent()
        }
    }
}