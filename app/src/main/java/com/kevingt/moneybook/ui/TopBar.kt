package com.kevingt.moneybook.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TopBar(
    title: String,
    navigateBack: (() -> Unit)? = null,
    menuActions: List<MenuAction>? = null,
    dropdownMenu: @Composable (() -> Unit)? = null
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary)
    ) {

        if (navigateBack != null) {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }

        Text(
            text = title,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.weight(1F)
        )

        menuActions?.forEach { action ->
            IconButton(onClick = action.onClick) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.description,
                    tint = Color.White,
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
private fun TopBar_Preview() = TopBar(title = "Money Book")

@Preview
@Composable
private fun TopBarWithBack_Preview() = TopBar(
    title = "Money Book",
    navigateBack = {},
    menuActions = listOf(
        MenuAction(Icons.Filled.Edit, "") {},
        MenuAction(Icons.Filled.Delete, "") {},
    )
)