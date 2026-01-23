package com.kevlina.budgetplus.core.common.di

import android.content.Context

interface HasServiceProvider {
    fun <T> resolve(): T
}

fun <T> Context.resolveGraphExtensionFactory(): T {
    return (applicationContext as HasServiceProvider).resolve()
}