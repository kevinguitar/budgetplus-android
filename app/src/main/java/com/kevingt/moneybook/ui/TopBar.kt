package com.kevingt.moneybook.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevingt.moneybook.R

@Composable
fun TopBar(
    title: String?,
    titleContent: @Composable (() -> Unit)? = null,
    navigateBack: (() -> Unit)? = null,
    menuActions: List<MenuAction>? = null,
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
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }

        when {
            title != null -> Text(
                text = title,
                style = MaterialTheme.typography.h5,
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

        menuActions?.forEach { action ->
            IconButton(
                onClick = action.onClick,
                modifier = Modifier.apply {
                    if (action.onPositioned != null) {
                        onGloballyPositioned(action.onPositioned)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = action.iconRes),
                    contentDescription = action.description,
                    tint = LocalAppColors.current.light,
                )
            }
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
        menuActions = listOf(
            MenuAction(R.drawable.ic_edit, "", onClick = {}),
            MenuAction(R.drawable.ic_invite, "", onClick = {}),
        )
    )
}