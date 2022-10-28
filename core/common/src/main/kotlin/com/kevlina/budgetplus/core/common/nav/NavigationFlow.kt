package com.kevlina.budgetplus.core.common.nav

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.consumeEach

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