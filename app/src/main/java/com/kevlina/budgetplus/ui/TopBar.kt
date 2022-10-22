package com.kevlina.budgetplus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DriveFileRenameOutline
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.R

@Composable
fun TopBar(
    title: String?,
    titleContent: @Composable (() -> Unit)? = null,
    navigateBack: (() -> Unit)? = null,
    menuActions: @Composable (() -> Unit)? = null,
    dropdownMenu: @Composable (() -> Unit)? = null
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(color = LocalAppColors.current.primaryDark)
    ) {

        if (navigateBack != null) {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.cta_back),
                    tint = LocalAppColors.current.light
                )
            }

            // Override the system back with navigateBack behavior
            BackHandler(enabled = true, onBack = navigateBack)
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }

        when {
            title != null -> AppText(
                text = title,
                fontSize = FontSize.Header,
                fontWeight = FontWeight.SemiBold,
                color = LocalAppColors.current.light,
                modifier = Modifier.weight(1F)
            )

            titleContent != null -> Box(
                modifier = Modifier.weight(1F),
            ) {
                titleContent()
            }
        }

        if (menuActions != null) {
            menuActions()
        }

        if (dropdownMenu != null) {
            dropdownMenu()
        }
    }
}

@Preview
@Composable
private fun TopBar_Preview() = AppTheme {
    TopBar(title = "Money Book")
}

@Preview
@Composable
private fun TopBarWithBack_Preview() = AppTheme {
    TopBar(
        title = "Money Book",
        navigateBack = {},
        menuActions = {
            MenuAction(imageVector = Icons.Rounded.DriveFileRenameOutline, description = "")
            MenuAction(imageVector = Icons.Rounded.GroupAdd, description = "")
        }
    )
}