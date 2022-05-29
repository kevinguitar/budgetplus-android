package com.kevingt.moneybook.book.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.utils.shortFormatted
import java.time.LocalDate

@Composable
fun OverviewScreen(navController: NavController) {

    val viewModel = hiltViewModel<OverviewViewModel>()

    val records by viewModel.records.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(records.orEmpty()) { item ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = LocalDate.ofEpochDay(item.date).shortFormatted)
                Text(text = item.category)
                Text(text = item.name)
                Text(text = item.price.toString())
            }
        }
    }
}