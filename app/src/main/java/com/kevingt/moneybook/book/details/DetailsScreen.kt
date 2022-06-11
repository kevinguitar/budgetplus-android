package com.kevingt.moneybook.book.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kevingt.moneybook.book.AddDest
import com.kevingt.moneybook.book.overview.vm.OverviewViewModel
import com.kevingt.moneybook.data.remote.Author
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.ui.TopBar
import com.kevingt.moneybook.utils.ARG_EDIT_RECORD
import com.kevingt.moneybook.utils.dollar
import com.kevingt.moneybook.utils.shortFormatted
import java.time.LocalDate

@Composable
fun DetailsScreen(
    navController: NavController,
    viewModel: OverviewViewModel,
    category: String?
) {

    requireNotNull(category) { "Category must be presented to show the details." }

    val recordGroups by viewModel.recordGroups.collectAsState()
    val records = recordGroups[category].orEmpty()
    val totalPrice = records.sumOf { it.price }.dollar

    Column {

        TopBar(
            title = "$category $totalPrice",
            navigateBack = { navController.navigateUp() }
        )

        LazyColumn(
            modifier = Modifier.padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(records) { item ->
                RecordCard(item = item) {
                    navController.currentBackStackEntry?.arguments?.putParcelable(ARG_EDIT_RECORD, item)
                    navController.navigate(AddDest.Record.route)
                }
            }
        }
    }
}

@Composable
fun RecordCard(
    item: Record,
    onEdit: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(text = LocalDate.ofEpochDay(item.date).shortFormatted)
        Text(
            text = item.name,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1F)
        )
        Text(text = item.price.dollar)
        Text(text = item.author?.name.orEmpty())
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = "Edit",
            modifier = Modifier.clickable(onClick = onEdit)
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
    onEdit = {}
)