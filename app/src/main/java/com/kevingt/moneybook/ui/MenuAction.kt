package com.kevingt.moneybook.ui

import androidx.compose.ui.graphics.vector.ImageVector

class MenuAction(
    val icon: ImageVector,
    val description: String,
    val onClick: () -> Unit
)