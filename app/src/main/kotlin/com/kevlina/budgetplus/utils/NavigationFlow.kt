package com.kevlina.budgetplus.utils

import android.app.Activity
import android.content.Context
import android.content.Intent

@Suppress("FunctionName")
fun NavigationFlow() = MutableEventFlow<NavigationInfo>()

fun EventFlow<NavigationInfo>.consume(context: Context) =
    consumeEach { info ->
        val intent = Intent(context, info.destination.java)
        intent.putExtras(info.bundle)
        context.startActivity(intent)

        if (info.finishCurrent) {
            (context as Activity).finish()
        }
    }