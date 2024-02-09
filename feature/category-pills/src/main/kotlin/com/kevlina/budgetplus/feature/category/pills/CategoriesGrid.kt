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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DriveFileRenameOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.rippleClick
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CategoriesGrid(
    uiState: CategoriesGridUiState,
    modifier: Modifier = Modifier,
) {

    val type by uiState.type.collectAsStateWithLifecycle()
    val expenseCategories by uiState.expenseCategories.collectAsStateWithLifecycle()
    val incomeCategories by uiState.incomeCategories.collectAsStateWithLifecycle()
    val selectedCategory by uiState.selectedCategory.collectAsStateWithLifecycle()

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
                    paddingValues = uiState.cardPaddingValues
                ) {
                    uiState.onCategorySelected(item)
                }
            }

            RecordType.Income -> incomeCategories.forEach { item ->
                CategoryCard(
                    category = item,
                    isSelected = selectedCategory == item,
                    paddingValues = uiState.cardPaddingValues
                ) {
                    uiState.onCategorySelected(item)
                }
            }
        }

        if (uiState.onEditClicked != null) {
            CategoryActionButton(
                icon = Icons.Rounded.DriveFileRenameOutline,
                name = stringResource(id = R.string.cta_edit),
                onClick = uiState.onEditClicked
            )
        }

        if (uiState.onAddClicked != null) {
            CategoryActionButton(
                icon = Icons.Rounded.Add,
                name = stringResource(id = R.string.cta_add),
                onClick = uiState.onAddClicked
            )
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
private fun CategoryActionButton(
    icon: ImageVector,
    name: String,
    onClick: () -> Unit,
) {
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
                imageVector = icon,
                contentDescription = stringResource(id = R.string.category_edit_title),
                tint = LocalAppColors.current.light,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = name,
                singleLine = true,
                color = LocalAppColors.current.light
            )
        }
    }
}

@Stable
class CategoriesGridUiState(
    val expenseCategories: StateFlow<List<String>>,
    val incomeCategories: StateFlow<List<String>>,
    val type: StateFlow<RecordType>,
    val selectedCategory: StateFlow<String?>,
    val onCategorySelected: (String) -> Unit,
    val onEditClicked: (() -> Unit)? = null,
    val onAddClicked: (() -> Unit)? = null,
    val cardPaddingValues: PaddingValues = cardPadding,
) {
    companion object {
        val preview = CategoriesGridUiState(
            type = MutableStateFlow(RecordType.Expense),
            expenseCategories = MutableStateFlow(listOf(
                "Food", "Daily", "Transport", "Entertainment", "Rent", "Mobile", "Utility", "Other"
            )),
            incomeCategories = MutableStateFlow(emptyList()),
            onCategorySelected = {},
            onEditClicked = {},
            selectedCategory = MutableStateFlow("Daily")
        )
    }
}

fun CategoriesViewModel.toUiState(
    type: StateFlow<RecordType>,
    selectedCategory: StateFlow<String?> = category,
    onCategorySelected: (String) -> Unit = ::setCategory,
    onEditClicked: (() -> Unit)? = null,
    onAddClicked: (() -> Unit)? = null,
    cardPaddingValues: PaddingValues = cardPadding,
) = CategoriesGridUiState(
    expenseCategories = expenseCategories,
    incomeCategories = incomeCategories,
    type = type,
    selectedCategory = selectedCategory,
    onCategorySelected = onCategorySelected,
    onEditClicked = onEditClicked,
    onAddClicked = onAddClicked,
    cardPaddingValues = cardPaddingValues
)

@Preview
@Composable
private fun CategoryGrid_Preview() = AppTheme {
    CategoriesGrid(
        CategoriesGridUiState.preview,
        modifier = Modifier
            .background(LocalAppColors.current.light)
            .padding(all = 16.dp)
    )
}