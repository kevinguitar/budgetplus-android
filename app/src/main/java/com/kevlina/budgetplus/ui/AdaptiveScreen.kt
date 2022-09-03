package com.kevlina.budgetplus.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ColumnScope.AdaptiveScreen(
    wideContent: @Composable () -> Unit,
    regularContent: @Composable () -> Unit,
    packedContent: @Composable () -> Unit = regularContent,
    extraContent: @Composable (BoxScope.() -> Unit)? = null
) {

    BoxWithConstraints(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .weight(1F)
    ) {

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