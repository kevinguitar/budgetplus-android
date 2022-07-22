package com.kevingt.moneybook.book.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.overview.vm.OverviewViewModel
import com.kevingt.moneybook.data.remote.Author
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.ui.LocalAppColors
import com.kevingt.moneybook.ui.MenuAction
import com.kevingt.moneybook.ui.TopBar
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
    var editRecordDialog by remember { mutableStateOf<Record?>(null) }

    val recordGroups by viewModel.recordGroups.collectAsState()
    val records = recordGroups[category].orEmpty()
    val totalPrice = records.sumOf { it.price }.dollar

    Column {

        TopBar(
            title = "$category $totalPrice",
            navigateBack = { navController.navigateUp() },
            menuActions = {
                when (sortMode) {
                    RecordsSortMode.Date -> MenuAction(
                        iconRes = R.drawable.ic_sort_date,
                        description = stringResource(id = R.string.overview_sort_by_price),
                        onClick = { sortMode = RecordsSortMode.Price }
                    )
                    RecordsSortMode.Price -> MenuAction(
                        iconRes = R.drawable.ic_dollar,
                        description = stringResource(id = R.string.overview_sort_by_date),
                        onClick = { sortMode = RecordsSortMode.Date }
                    )
                }
            }
        )

        val sortedRecords = when (sortMode) {
            RecordsSortMode.Date -> records.sortedByDescending { it.date }
            RecordsSortMode.Price -> records.sortedByDescending { it.price }
        }

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            items(sortedRecords) { item ->
                RecordCard(item = item) {
                    editRecordDialog = item
                }
            }
        }
    }

    val editRecord = editRecordDialog
    if (editRecord != null) {

        EditRecordDialog(
            editRecord = editRecord,
            onDismiss = { editRecordDialog = null }
        )
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = LocalAppColors.current.dark),
                onClick = onEdit
            )
            .padding(vertical = 12.dp)
    ) {

        Text(
            text = LocalDate.ofEpochDay(item.date).shortFormatted,
            color = LocalAppColors.current.dark,
        )

        Text(
            text = item.name,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            color = LocalAppColors.current.dark,
            modifier = Modifier.weight(1F)
        )

        Text(
            text = item.price.dollar,
            color = LocalAppColors.current.dark,
        )

        Text(
            text = item.author?.name.orEmpty(),
            color = LocalAppColors.current.dark,
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