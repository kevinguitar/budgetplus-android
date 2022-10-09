package buildSrc.src.main.kotlin

import kotlin.math.pow

object AppConfig {

    // Budget+ application id
    const val APPLICATION_ID = "com.kevlina.budgetplus"

    // Budget+ version
    const val APP_VERSION = "1.1.9"

    // Minimal Android sdk version
    const val MIN_ANDROID_SDK_VERSION = 23

    // Compile and target sdk version
    const val ANDROID_SDK_VERSION = 33

    /**
     *  Major version * 10^6
     *  Minor version * 10^3
     *  Bugfix version * 10^0
     *
     *  "1.1.4"     to 1001004
     *  "3.100.1"   to 3100001
     *  "10.52.39"  to 10052039
     */
    val appVersionCode: Int
        get() = APP_VERSION
            .split(".")
            .reversed()
            .mapIndexed { index, num ->
                num.toInt() * 10.0.pow(index * 3).toInt()
            }
            .sum()
}