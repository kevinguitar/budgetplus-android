package com.kevlina.budgetplus.book.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.bubble.vm.BubbleDest
import com.kevlina.budgetplus.book.details.vm.DetailsViewModel
import com.kevlina.budgetplus.data.remote.Author
import com.kevlina.budgetplus.data.remote.Record
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.monetize.AdsBanner
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.AppTheme
import com.kevlina.budgetplus.ui.FontSize
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.ui.MenuAction
import com.kevlina.budgetplus.ui.TopBar
import com.kevlina.budgetplus.utils.Navigator
import com.kevlina.budgetplus.utils.dollar
import com.kevlina.budgetplus.utils.rippleClick
import com.kevlina.budgetplus.utils.shortFormatted
import java.time.LocalDate

@Composable
fun DetailsScreen(
    navigator: Navigator,
    vm: DetailsViewModel,
) {

    var editRecordDialog by remember { mutableStateOf<Record?>(null) }

    val records by vm.records.collectAsState()
    val sortMode by vm.sortMode.collectAsState()
    val isHideAds by vm.isHideAds.collectAsState()

    val totalPrice = records.sumOf { it.price }.dollar

    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(
            title = stringResource(id = R.string.overview_details_title, vm.category, totalPrice),
            navigateBack = { navigator.navigateUp() },
            menuActions = {
                val modifier = Modifier.onGloballyPositioned {
                    vm.highlightSortingButton(
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
                        onClick = { vm.setSortMode(RecordsSortMode.Price) },
                        modifier = modifier
                    )

                    RecordsSortMode.Price -> MenuAction(
                        iconRes = R.drawable.ic_dollar,
                        description = stringResource(id = R.string.overview_sort_by_date),
                        onClick = { vm.setSortMode(RecordsSortMode.Date) },
                        modifier = modifier
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .width(AppTheme.maxContentWidth)
                .align(Alignment.CenterHorizontally)
                .weight(1F)
        ) {

            itemsIndexed(records) { index, item ->
                RecordCard(
                    item = item,
                    isLast = index == records.lastIndex
                ) {
                    editRecordDialog = item
                }
            }
        }

        if (!isHideAds) {
            AdsBanner()
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
    onEdit: () -> Unit,
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
                fontSize = FontSize.SemiLarge,
                fontWeight = FontWeight.Medium,
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