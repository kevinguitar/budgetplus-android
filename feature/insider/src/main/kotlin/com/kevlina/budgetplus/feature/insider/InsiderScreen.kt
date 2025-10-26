package com.kevlina.budgetplus.feature.insider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.feature.insider.ui.InsiderContent

@Composable
fun InsiderScreen(
    openPushNotifications: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.light)
    ) {

        TopBar(
            title = stringResource(id = R.string.insider_title),
            menuActions = {
                MenuAction(
                    imageVector = Icons.Rounded.NotificationAdd,
                    description = stringResource(id = R.string.cta_add),
                    onClick = openPushNotifications,
                )
            }
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {
            InsiderContent()
        }
    }
}