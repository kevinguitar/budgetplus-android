package com.kevlina.budgetplus.core.common

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.StringResource
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@ContributesBinding(AppScope::class)
class ShareHelperImpl : ShareHelper {

    override suspend fun share(title: StringResource, text: String) {
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return

        withContext(Dispatchers.Main) {
            val activityViewController = UIActivityViewController(
                activityItems = listOf(text),
                applicationActivities = null
            )

            rootViewController.presentViewController(
                viewControllerToPresent = activityViewController,
                animated = true,
                completion = null
            )
        }
    }
}