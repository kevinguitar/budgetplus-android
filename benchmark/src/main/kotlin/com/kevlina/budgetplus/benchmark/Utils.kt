package com.kevlina.budgetplus.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

internal const val APP_PACKAGE = "com.kevlina.budgetplus"
private const val UI_TIMEOUT = 5_000L

internal fun MacrobenchmarkScope.authorize() {
    // Dismiss bubbles
    waitForTextAndClick("Invite fellows to track expenses together!")
    waitForTextAndClick("Long tap to record your expenses by voice")

    waitForText("Expense")
}

private fun MacrobenchmarkScope.waitForText(text: String): Boolean {
    return device.wait(Until.hasObject(By.text(text)), UI_TIMEOUT)
}

private fun MacrobenchmarkScope.waitForTextAndClick(text: String): Boolean {
    val isVisible = waitForText(text)
    if (isVisible) {
        device.findObject(By.text(text)).click()
    }
    return isVisible
}