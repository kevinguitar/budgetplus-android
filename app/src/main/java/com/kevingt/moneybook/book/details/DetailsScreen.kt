package com.kevingt.moneybook.book.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.bubble.vm.BubbleDest
import com.kevingt.moneybook.book.overview.vm.OverviewViewModel
import com.kevingt.moneybook.data.remote.Author
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.monetize.AdsBanner
import com.kevingt.moneybook.monetize.AdsMode
import com.kevingt.moneybook.ui.*
import com.kevingt.moneybook.utils.dollar
import com.kevingt.moneybook.utils.rippleClick
import com.kevingt.moneybook.utils.shortFormatted
import java.time.LocalDate

@Composable
fun DetailsScreen(
    navController: NavController,
    viewModel: OverviewViewModel,
    category: String?
) {

    requireNotNull(category) { "Category must be presented to show the details." }

    var editRecordDialog by remember { mutableStateOf<Record?>(null) }

    val recordGroups by viewModel.recordGroups.collectAsState()
    val sortMode by viewModel.sortMode.collectAsState()
    val isHideAds by viewModel.isHideAds.collectAsState()

    val records = recordGroups[category].orEmpty()
    val totalPrice = records.sumOf { it.price }.dollar

    Column {

        TopBar(
            title = "$category $totalPrice",
            navigateBack = { navController.navigateUp() },
            menuActions = {
                val modifier = Modifier.onGloballyPositioned {
                    viewModel.highlightSortingButton(
                        BubbleDest.RecordsSorting(
                            size = it.size,
                            offset = it.positionInRoot()
                        )
                    )
                }

                when (sortMode) {
                    RecordsSortMode.Date -> MenuAction(
                        iconRes = R.drawable.ic_sort_date,
                        description = stringResource(id = R.string.overview_sort_by_price),
                        onClick = { viewModel.setSortMode(RecordsSortMode.Price) },
                        modifier = modifier
                    )
                    RecordsSortMode.Price -> MenuAction(
                        iconRes = R.drawable.ic_dollar,
                        description = stringResource(id = R.string.overview_sort_by_date),
                        onClick = { viewModel.setSortMode(RecordsSortMode.Date) },
                        modifier = modifier
                    )
                }
            }
        )

        val sortedRecords = when (sortMode) {
            RecordsSortMode.Date -> records.sortedByDescending { it.date }
            RecordsSortMode.Price -> records.sortedByDescending { it.price }
        }

        LazyColumn(modifier = Modifier.weight(1F)) {

            itemsIndexed(sortedRecords) { index, item ->
                RecordCard(
                    item = item,
                    isLast = index == sortedRecords.lastIndex
                ) {
                    editRecordDialog = item
                }
            }
        }

        if (!isHideAds) {
            AdsBanner(mode = AdsMode.Banner)
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
    isLast: Boolean,
    onEdit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .rippleClick(
                color = LocalAppColors.current.dark,
                onClick = onEdit
            )
            .padding(horizontal = 16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1F),
            ) {

                AppText(
                    text = item.name,
                    fontSize = FontSize.SemiLarge,
                    fontWeight = FontWeight.SemiBold,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AppText(
                        text = LocalDate.ofEpochDay(item.date).shortFormatted,
                    )

                    AppText(
                        text = item.author?.name.orEmpty(),
                        fontSize = FontSize.Small,
                        color = LocalAppColors.current.light,
                        modifier = Modifier
                            .background(
                                color = LocalAppColors.current.primary,
                                shape = CircleShape
                            )
                            .padding(vertical = 1.dp, horizontal = 8.dp)
                    )
                }

            }

            AppText(
                text = item.price.dollar,
                fontSize = FontSize.Large,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (!isLast) {
            Divider(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(color = LocalAppColors.current.primary)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordCard_Preview() = AppTheme {
    RecordCard(
        item = Record(
            type = RecordType.Income,
            date = LocalDate.now().toEpochDay(),
            category = "Food",
            name = "Fancy Restaurant",
            price = 453.93,
            author = Author(id = "", name = "Kevin")
        ),
        isLast = false,
        onEdit = {}
    )
}