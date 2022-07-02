package com.kevingt.moneybook.book.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.AddDest
import com.kevingt.moneybook.book.overview.vm.OverviewViewModel
import com.kevingt.moneybook.data.remote.Author
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.ui.MenuAction
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

    var sortMode by remember { mutableStateOf(RecordsSortMode.Date) }

    val recordGroups by viewModel.recordGroups.collectAsState()
    val records = recordGroups[category].orEmpty()
    val totalPrice = records.sumOf { it.price }.dollar

    Column {

        TopBar(
            title = "$category $totalPrice",
            navigateBack = { navController.navigateUp() },
            menuActions = when (sortMode) {
                RecordsSortMode.Date -> listOf(MenuAction(
                    icon = Icons.Filled.Star,
                    description = stringResource(id = R.string.overview_sort_by_price),
                    onClick = { sortMode = RecordsSortMode.Price }
                ))
                RecordsSortMode.Price -> listOf(MenuAction(
                    icon = Icons.Filled.DateRange,
                    description = stringResource(id = R.string.overview_sort_by_date),
                    onClick = { sortMode = RecordsSortMode.Date }
                ))
            }
        )

        val sortedRecords = when (sortMode) {
            RecordsSortMode.Date -> records.sortedByDescending { it.date }
            RecordsSortMode.Price -> records.sortedByDescending { it.price }
        }

        LazyColumn(
            modifier = Modifier.padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(sortedRecords) { item ->
                RecordCard(item = item) {
                    navController.currentBackStackEntry?.arguments?.putParcelable(
                        ARG_EDIT_RECORD,
                        item
                    )
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
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = stringResource(id = R.string.cta_edit),
            )
        }
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