package com.kevlina.budgetplus.feature.record.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.dollar
import com.kevlina.budgetplus.core.common.shortFormatted
import com.kevlina.budgetplus.core.data.remote.Author
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.DropdownItem
import com.kevlina.budgetplus.core.ui.DropdownMenu
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.rippleClick
import java.time.LocalDate

@Immutable
class RecordCardUiState(
    val item: Record,
    val isLast: Boolean,
    val canEdit: Boolean,
    val showCategory: Boolean,
    val showAuthor: Boolean,
    val onEdit: () -> Unit,
    val onDuplicate: () -> Unit,
    val onDelete: () -> Unit,
)

@Composable
fun RecordCard(
    uiState: RecordCardUiState,
    modifier: Modifier = Modifier,
) {

    val item = uiState.item
    var isMenuShown by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .rippleClick(
                color = LocalAppColors.current.dark,
                onClick = if (uiState.canEdit) uiState.onEdit else {
                    {}
                },
                onLongClick = { isMenuShown = true }
            )
            .padding(horizontal = 16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1F),
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (item.isBatched) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            tint = LocalAppColors.current.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = item.name,
                        fontSize = FontSize.SemiLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = LocalDate.ofEpochDay(item.date).shortFormatted,
                    )

                    if (uiState.showCategory) {
                        PillLabel(text = item.category)
                    }

                    if (uiState.showAuthor) {
                        PillLabel(text = item.author?.name.orEmpty())
                    }
                }
            }

            Text(
                text = item.price.dollar,
                fontSize = FontSize.SemiLarge,
                fontWeight = FontWeight.Medium,
            )
        }

        if (!uiState.isLast) {
            Spacer(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(color = LocalAppColors.current.primary)
            )
        }

        // Long click to display the menu
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.BottomEnd)
        ) {
            DropdownMenu(
                expanded = isMenuShown,
                onDismissRequest = { isMenuShown = false },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(color = LocalAppColors.current.light)
            ) {

                DropdownItem(
                    name = stringResource(id = R.string.cta_duplicate),
                ) {
                    isMenuShown = false
                    uiState.onDuplicate()
                }

                if (uiState.canEdit) {
                    DropdownItem(
                        name = stringResource(id = R.string.cta_delete),
                    ) {
                        isMenuShown = false
                        uiState.onDelete()
                    }
                }
            }
        }
    }
}

@Composable
private fun PillLabel(text: String) {
    Text(
        text = text,
        fontSize = FontSize.Small,
        color = LocalAppColors.current.light,
        singleLine = true,
        modifier = Modifier
            .background(
                color = LocalAppColors.current.primary,
                shape = CircleShape
            )
            .padding(vertical = 1.dp, horizontal = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun RecordCard_Preview() = AppTheme {
    RecordCard(uiState = RecordCardUiState(
        item = Record(
            type = RecordType.Income,
            date = LocalDate.now().toEpochDay(),
            category = "Food",
            name = "Fancy Restaurant",
            price = 453.93,
            author = Author(id = "", name = "Kevin")
        ),
        isLast = false,
        canEdit = true,
        showCategory = true,
        showAuthor = true,
        onEdit = {},
        onDuplicate = {},
        onDelete = {}
    ))
}