package com.kevlina.budgetplus.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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

@Composable
fun MenuAction(
    @DrawableRes iconRes: Int,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = description,
            tint = LocalAppColors.current.light,
        )
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
            MenuAction(iconRes = R.drawable.ic_edit, description = "")
            MenuAction(iconRes = R.drawable.ic_invite, description = "")
        }
    )
}