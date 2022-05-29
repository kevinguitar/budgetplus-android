package com.kevingt.moneybook.book.record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.ui.TopBar

@Composable
fun RecordScreen(navController: NavController) {

    val viewModel = hiltViewModel<RecordViewModel>()

    val bookName by viewModel.bookName.collectAsState(initial = null)
    val context = LocalContext.current

    Column {

        TopBar(
            title = bookName.orEmpty(),
            menuItem = {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier.clickable { viewModel.shareJoinLink(context) }
                )
            }
        )

        RecordInfo(navController = navController)

    }
}