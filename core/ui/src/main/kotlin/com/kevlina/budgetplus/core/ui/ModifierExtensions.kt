package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color

inline fun Modifier.thenIf(condition: Boolean, modifierProvider: () -> Modifier): Modifier {
    return if (condition) {
        then(modifierProvider())
    } else {
        this
    }
}

fun Modifier.rippleClick(
    color: Color = Color.Unspecified,
    borderless: Boolean = false,
    onClick: () -> Unit,
) = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = !borderless, color = color),
        onClick = onClick
    )
}

fun Modifier.blockClicks() = composed {
    clickable(
        enabled = true,
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = {}
    )
}