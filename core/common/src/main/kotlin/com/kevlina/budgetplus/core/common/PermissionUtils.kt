package com.kevlina.budgetplus.core.common

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Activity.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) ==
        PackageManager.PERMISSION_GRANTED
}

fun Activity.shouldShowRationale(permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}