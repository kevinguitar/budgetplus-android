package com.kevlina.budgetplus.core.common.nav

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.consumeEach
import kotlinx.coroutines.flow.collect

@Suppress("FunctionName")
fun NavigationFlow() = MutableEventFlow<NavigationInfo>()

@SuppressLint("ComposableNaming")
@Composable
fun EventFlow<NavigationInfo>.consumeAsEffect() {
    val context = LocalContext.current
    LaunchedEffect(this) {
        consumeEach { info ->
            val intent = Intent(context, info.destination.java)
            intent.putExtras(info.bundle)
            context.startActivity(intent)

            if (info.finishCurrent) {
                (context as Activity).finish()
            }
        }.collect()
    }
}