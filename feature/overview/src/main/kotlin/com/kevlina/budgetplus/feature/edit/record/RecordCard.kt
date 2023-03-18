package com.kevlina.budgetplus.feature.edit.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.dollar
import com.kevlina.budgetplus.core.common.shortFormatted
import com.kevlina.budgetplus.core.data.remote.Author
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.rippleClick
import com.kevlina.budgetplus.core.ui.thenIf
import java.time.LocalDate

@Composable
internal fun RecordCard(
    item: Record,
    isLast: Boolean,
    canEdit: Boolean,
    showCategory: Boolean,
    onEdit: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .thenIf(canEdit) {
                Modifier.rippleClick(
                    color = LocalAppColors.current.dark,
                    onClick = onEdit
                )
            }
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

                    if (showCategory) {
                        PillLabel(text = item.category)
                    }

                    PillLabel(text = item.author?.name.orEmpty())
                }
            }

            Text(
                text = item.price.dollar,
                fontSize = FontSize.SemiLarge,
                fontWeight = FontWeight.Medium,
            )
        }

        if (!isLast) {
            Spacer(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(color = LocalAppColors.current.primary)
            )
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
        canEdit = true,
        showCategory = true,
        onEdit = {}
    )
}