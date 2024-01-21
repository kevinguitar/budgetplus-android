package com.kevlina.budgetplus.core.common.nav

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.consumeEach
import kotlinx.coroutines.flow.collect

@Suppress("FunctionName")
fun NavigationFlow() = MutableEventFlow<NavigationAction>()

@SuppressLint("ComposableNaming")
@Composable
fun EventFlow<NavigationAction>.consumeAsEffect() {
    val context = LocalContext.current
    LaunchedEffect(this) {
        consumeEach { action ->
            context.startActivity(action.intent)

            if (action.finishCurrent) {
                (context as Activity).finish()
            }
        }.collect()
    }
}