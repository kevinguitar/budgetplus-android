package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.RecordTypeTab
import com.kevlina.budgetplus.core.ui.SingleDatePicker
import com.kevlina.budgetplus.core.ui.TOP_BAR_DARKEN_FACTOR
import com.kevlina.budgetplus.core.ui.TextField
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.core.ui.darken
import com.kevlina.budgetplus.feature.category.pills.CategoriesGrid
import com.kevlina.budgetplus.feature.category.pills.CategoriesGridUiState
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.util.Currency
import java.util.Locale

@Composable
internal fun TonePreview(
    modifier: Modifier = Modifier,
) {

    val expenseCategoriesArray = stringArrayResource(id = R.array.default_expense_categories)
    val expenseCategories = remember { expenseCategoriesArray.toList() }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .clip(AppTheme.cardShape)
            .border(
                width = 1.dp,
                color = LocalAppColors.current.primary.darken(TOP_BAR_DARKEN_FACTOR),
                shape = RoundedCornerShape(ColorToneConstants.CardCornerRadius)
            )
    ) {

        TopBar(
            title = stringResource(id = R.string.color_tone_preview_title),
            menuActions = {
                MenuAction(
                    imageVector = Icons.Rounded.GroupAdd,
                    description = stringResource(id = R.string.cta_invite),
                )

                MenuAction(
                    imageVector = Icons.Rounded.Settings,
                    description = stringResource(id = R.string.settings_description),
                )
            }
        )

        RecordTypeTab(
            selected = RecordType.Expense,
            onTypeSelected = { },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        CategoriesGrid(
            uiState = CategoriesGridUiState(
                expenseCategories = MutableStateFlow(expenseCategories),
                incomeCategories = MutableStateFlow(emptyList()),
                type = MutableStateFlow(RecordType.Expense),
                selectedCategory = MutableStateFlow(expenseCategories.first()),
                onCategorySelected = {},
                cardPaddingValues = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        TextField(
            value = "",
            onValueChange = { },
            title = stringResource(id = R.string.record_note),
            placeholder = stringResource(id = R.string.record_note_placeholder_expense),
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {

            SingleDatePicker(
                date = LocalDate.now(),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            TextField(
                value = "1,680",
                onValueChange = {},
                fontSize = FontSize.Header,
                letterSpacing = 0.5.sp,
                enabled = false,
                title = Currency.getInstance(Locale.getDefault()).getSymbol(Locale.getDefault()),
                modifier = Modifier.weight(1F)
            )
        }
    }
}

@Preview
@Composable
private fun TonePreview_Preview() = AppTheme {
    TonePreview()
}