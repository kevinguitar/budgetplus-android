package com.kevlina.budgetplus.feature.category.pills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DriveFileRenameOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.rippleClick
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun CategoriesGrid(
    type: RecordType,
    expenseCategories: ImmutableList<String>,
    incomeCategories: ImmutableList<String>,
    modifier: Modifier = Modifier,
    onCategorySelected: (String) -> Unit,
    onEditClicked: (() -> Unit)? = null,
    selectedCategory: String? = null,
    cardPaddingValues: PaddingValues = cardPadding,
) {

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
        modifier = modifier
    ) {

        when (type) {
            RecordType.Expense -> expenseCategories.forEach { item ->
                CategoryCard(
                    category = item,
                    isSelected = selectedCategory == item,
                    paddingValues = cardPaddingValues
                ) {
                    onCategorySelected(item)
                }
            }

            RecordType.Income -> incomeCategories.forEach { item ->
                CategoryCard(
                    category = item,
                    isSelected = selectedCategory == item,
                    paddingValues = cardPaddingValues
                ) {
                    onCategorySelected(item)
                }
            }
        }

        if (onEditClicked != null) {
            EditCategoryButton(onEditClicked)
        }
    }
}

private val cardPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)

@Composable
fun CategoryCard(
    category: String,
    isSelected: Boolean,
    paddingValues: PaddingValues = cardPadding,
    onClick: () -> Unit,
) {

    Box(
        modifier = Modifier
            .background(
                shape = AppTheme.cardShape,
                color = if (isSelected) {
                    LocalAppColors.current.dark
                } else {
                    LocalAppColors.current.primary
                }
            )
            .clip(AppTheme.cardShape)
            .rippleClick(onClick = onClick)
    ) {

        Text(
            text = category,
            singleLine = true,
            color = LocalAppColors.current.light,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun EditCategoryButton(onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .background(
                shape = AppTheme.cardShape,
                color = LocalAppColors.current.primary
            )
            .clip(AppTheme.cardShape)
            .rippleClick(onClick = onClick)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(cardPadding)
        ) {

            Icon(
                imageVector = Icons.Rounded.DriveFileRenameOutline,
                contentDescription = stringResource(id = R.string.category_edit_title),
                tint = LocalAppColors.current.light,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = stringResource(id = R.string.cta_edit),
                singleLine = true,
                color = LocalAppColors.current.light
            )
        }
    }
}

@Preview
@Composable
private fun CategoryGrid_Preview() = AppTheme {
    CategoriesGrid(
        type = RecordType.Income,
        expenseCategories = persistentListOf(),
        incomeCategories = persistentListOf("Salary", "Bonus", "Cashback", "Others"),
        onCategorySelected = {},
        onEditClicked = {},
        selectedCategory = "Bonus"
    )
}