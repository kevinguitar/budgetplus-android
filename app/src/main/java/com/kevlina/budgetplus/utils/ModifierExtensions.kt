package com.kevlina.budgetplus.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
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
    onClick: () -> Unit
) = composed {
    clickable(
        interactionSource = MutableInteractionSource(),
        indication = rememberRipple(color = color),
        onClick = onClick
    )
}