package com.kevingt.moneybook.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.kevingt.moneybook.R
import com.kevingt.moneybook.data.remote.RecordType

@Composable
fun CategoriesGrid(
    type: RecordType,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectedCategory: String? = null,
    actionBtn: CategoriesActionBtn? = null
) {

    val viewModel = hiltViewModel<CategoriesViewModel>()

    val expenseCategories by viewModel.expenseCategories.collectAsState()
    val incomeCategories by viewModel.incomeCategories.collectAsState()

    FlowRow(
        mainAxisSize = SizeMode.Expand,
        mainAxisSpacing = 12.dp,
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

        if (actionBtn != null) {

            CategoryAction(actionBtn = actionBtn)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryCard(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            LocalAppColors.current.dark
        } else {
            LocalAppColors.current.primary
        },
        onClick = onClick
    ) {

        Text(
            text = category,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            color = LocalAppColors.current.light,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

sealed class CategoriesActionBtn {
    abstract val onClick: () -> Unit

    class Add(override val onClick: () -> Unit) : CategoriesActionBtn()
    class Edit(override val onClick: () -> Unit) : CategoriesActionBtn()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategoryAction(
    actionBtn: CategoriesActionBtn
) {

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = LocalAppColors.current.primary,
        onClick = actionBtn.onClick
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            when (actionBtn) {
                is CategoriesActionBtn.Add -> {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.cta_add),
                        tint = LocalAppColors.current.light,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.cta_add),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = LocalAppColors.current.light
                    )
                }
                is CategoriesActionBtn.Edit -> {

                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(id = R.string.category_edit_title),
                        tint = LocalAppColors.current.light,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.cta_edit),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = LocalAppColors.current.light
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CategoryCard_Preview() =
    CategoryCard(category = "Food", isSelected = true, onClick = {})

@Preview
@Composable
private fun EditCategoriesCard_Preview() = CategoryAction(CategoriesActionBtn.Edit {})