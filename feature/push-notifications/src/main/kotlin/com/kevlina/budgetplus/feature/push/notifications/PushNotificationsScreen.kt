package com.kevlina.budgetplus.feature.push.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.feature.push.notifications.ui.PushNotificationsContent

@Composable
fun PushNotificationsScreen(
    navigateUp: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.light)
    ) {

        TopBar(
            title = stringResource(id = R.string.push_notif_title),
            navigateUp = navigateUp,
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {
            PushNotificationsContent(
                navigateUp = navigateUp
            )
        }
    }
}