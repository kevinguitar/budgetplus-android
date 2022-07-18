package com.kevingt.moneybook.ui

import androidx.annotation.DrawableRes
import androidx.compose.ui.layout.LayoutCoordinates

class MenuAction(
    @DrawableRes val iconRes: Int,
    val description: String,
    val onClick: () -> Unit,
    val onPositioned: ((LayoutCoordinates) -> Unit)? = null
)