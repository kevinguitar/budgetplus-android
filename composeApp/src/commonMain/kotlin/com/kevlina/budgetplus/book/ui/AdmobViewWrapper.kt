package com.kevlina.budgetplus.book.ui

import androidx.compose.runtime.Composable

@Composable
internal expect fun AdmobViewWrapper(
    isAdMobInitialized: Boolean,
    bannerId: String,
    onStateUpdate: (AdBannerState) -> Unit
)