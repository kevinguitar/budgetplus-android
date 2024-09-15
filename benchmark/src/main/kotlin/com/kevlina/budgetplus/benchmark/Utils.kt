package com.kevlina.budgetplus.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

internal const val APP_PACKAGE = "com.kevlina.budgetplus"
private const val UI_TIMEOUT = 1_000L

internal fun MacrobenchmarkScope.authorize() {
    if (!waitForText("Welcome to Budget+")) {
        return
    }

    // Login with Google account
//    waitForTextAndClick("Continue as Kevin")
    waitForTextAndClick("Continue with Google")
    waitForTextAndClick("kevin.chiu@bandlab.com")

    // Record screen
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