package com.kevlina.budgetplus.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

internal const val APP_PACKAGE = "com.kevlina.budgetplus"

internal fun MacrobenchmarkScope.authorize() {
    val isVisible = device.wait(Until.hasObject(By.text("Continue as Kevin")), 3_000)
    if (isVisible) {
        device.findObject(By.text("Continue as Kevin")).click()
        device.wait(Until.hasObject(By.text("Expense")), 3_000)
    }
}