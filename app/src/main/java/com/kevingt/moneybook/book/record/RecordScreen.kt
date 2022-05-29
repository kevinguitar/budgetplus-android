package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.ui.TopBar

@Composable
fun RecordScreen(navController: NavController) {

    val viewModel = hiltViewModel<RecordViewModel>()
    val type by viewModel.type.collectAsState()

    Column {

        TopBar(title = "Record ${type.name}")

        RecordInfo(navController = navController)

    }
}