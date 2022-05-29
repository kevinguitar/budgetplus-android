package com.kevingt.moneybook.book.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.data.remote.Author
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.ui.TopBar
import com.kevingt.moneybook.utils.shortFormatted
import java.time.LocalDate

@Composable
fun OverviewScreen(navController: NavController) {

    val viewModel = hiltViewModel<OverviewViewModel>()

    val records by viewModel.records.collectAsState()

    Column {

        TopBar(title = "Overview")

        LazyColumn(
            modifier = Modifier.padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(records.orEmpty()) { item ->
                RecordCard(item = item, onDelete = viewModel::deleteRecord)
            }
        }
    }
}

@Composable
fun RecordCard(
    item: Record,
    onDelete: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = LocalDate.ofEpochDay(item.date).shortFormatted)
            Text(text = item.category)
        }
        Text(
            text = item.name,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1F)
        )
        Text(text = item.price.toString())
        Text(text = item.author?.name.orEmpty())
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "Delete",
            modifier = Modifier.clickable { onDelete(item.id) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordCard_Preview() = RecordCard(
    item = Record(
        type = RecordType.Income,
        date = LocalDate.now().toEpochDay(),
        category = "Food",
        name = "Fancy Restaurant",
        price = 453.93,
        author = Author(id = "", name = "Kevin")
    ),
    onDelete = {}
)