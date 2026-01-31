package com.kevlina.budgetplus.core.common

/**
 *  The action which needs to be executed on app start.
 */
interface AppStartAction {
    suspend fun onAppStart()
}