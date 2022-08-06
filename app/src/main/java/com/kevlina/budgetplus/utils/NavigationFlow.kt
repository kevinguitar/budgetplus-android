package com.kevlina.budgetplus.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach

@Suppress("FunctionName")
fun NavigationFlow() = MutableSharedFlow<NavigationInfo>(
    replay = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

fun Flow<NavigationInfo>.consumeEach(context: Context) =
    onEach { info ->
        val intent = Intent(context, info.destination.java)
        intent.putExtras(info.bundle)
        context.startActivity(intent)

        if (info.finishCurrent) {
            (context as Activity).finish()
        }
    }