package com.kevlina.budgetplus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.utils.rippleClick

@Composable
fun CategoriesGrid(
    type: RecordType,
    onCategorySelected: (String) -> Unit,
    onEditClicked: () -> Unit,
    modifier: Modifier = Modifier,
    selectedCategory: String? = null
) {

    val viewModel = hiltViewModel<CategoriesViewModel>()

    val expenseCategories by viewModel.expenseCategories.collectAsState()
    val incomeCategories by viewModel.incomeCategories.collectAsState()

    FlowRow(
        mainAxisSpacing = 12.dp,
        crossAxisSpacing = 12.dp,
        modifier = modifier
    ) {

        when (type) {
            RecordType.Expense -> expenseCategories.forEach { item ->
                CategoryCard(category = item, isSelected = selectedCategory == item) {
                    onCategorySelected(item)
                }
            }
            RecordType.Income -> incomeCategories.forEach { item ->
                CategoryCard(category = item, isSelected = selectedCategory == item) {
                    onCategorySelected(item)
                }
            }
        }

        EditCategoryButton(onEditClicked)
    }
}

private val cardShape = RoundedCornerShape(12.dp)
private val cardPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)

@Composable
fun CategoryCard(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .background(
                shape = cardShape,
                color = if (isSelected) {
                    LocalAppColors.current.dark
                } else {
                    LocalAppColors.current.primary
                }
            )
            .clip(cardShape)
            .rippleClick(onClick = onClick)
    ) {

        AppText(
            text = category,
            singleLine = true,
            color = LocalAppColors.current.light,
            modifier = Modifier.padding(cardPadding)
        )
    }
}

@Composable
private fun EditCategoryButton(onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .background(
                shape = cardShape,
                color = LocalAppColors.current.primary
            )
            .clip(cardShape)
            .rippleClick(onClick = onClick)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(cardPadding)
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = stringResource(id = R.string.category_edit_title),
                tint = LocalAppColors.current.light,
                modifier = Modifier.size(16.dp)
            )

            AppText(
                text = stringResource(id = R.string.cta_edit),
                singleLine = true,
                color = LocalAppColors.current.light
            )
        }
    }
}

@Preview
@Composable
private fun CategoryCard_Preview() = AppTheme {
    CategoryCard(category = "Food", isSelected = true, onClick = {})
}

@Preview
@Composable
private fun EditCategoriesCard_Preview() = AppTheme {
    EditCategoryButton {}
}