package com.kevlina.budgetplus.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

internal const val APP_PACKAGE = "com.kevlina.budgetplus"
private const val UI_TIMEOUT = 3_000L

internal fun MacrobenchmarkScope.authorize() {
    val isVisible = device.wait(Until.hasObject(By.text("Continue as Kevin")), UI_TIMEOUT)
    if (isVisible) {
        device.findObject(By.text("Continue as Kevin")).click()
        device.wait(Until.hasObject(By.text("Expense")), UI_TIMEOUT)
    }
}