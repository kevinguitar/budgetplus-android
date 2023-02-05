package com.kevlina.budgetplus.core.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat

fun ActivityResultLauncher<String>.requestNotificationPermission(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val permission = Manifest.permission.POST_NOTIFICATIONS
    val hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    val showRationale = shouldShowRequestPermissionRationale(context as Activity, permission)

    if (!hasPermission && !showRationale) {
        launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}