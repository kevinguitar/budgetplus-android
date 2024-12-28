package com.kevlina.budgetplus.benchmark

import android.os.Build
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SdkSuppress
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Use the latest SDK to generate the file
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.VANILLA_ICE_CREAM)
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startup() {
        var needAuthorize = true
        baselineProfileRule.collect(
            packageName = APP_PACKAGE,
            maxIterations = 10,
            profileBlock = {
                // This block defines the app's critical user journey. Here we are interested in
                // optimizing for app startup. But you can also navigate and scroll
                // through your most important UI.
                pressHome()
                startActivityAndWait()

                if (needAuthorize) {
                    authorize()
                    needAuthorize = false
                }
            }
        )
    }
}