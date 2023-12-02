package com.kevlina.budgetplus.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

internal const val APP_PACKAGE = "com.kevlina.budgetplus"
private const val UI_TIMEOUT = 1_000L

internal fun MacrobenchmarkScope.authorize() {
    if (!hasText("Welcome to Budget+")) {
        return
    }

    waitForTextAndClick("Continue with Google")
    waitForTextAndClick("kevin.chiu@bandlab.com")
    hasText("Expense")
}

private fun MacrobenchmarkScope.hasText(text: String): Boolean {
    return device.wait(Until.hasObject(By.text(text)), UI_TIMEOUT)
}

private fun MacrobenchmarkScope.waitForTextAndClick(text: String) {
    val isVisible = hasText(text)
    if (isVisible) {
        device.findObject(By.text(text)).click()
    }
}