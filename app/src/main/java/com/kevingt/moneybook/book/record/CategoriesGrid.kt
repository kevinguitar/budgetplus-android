package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.AddDest
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.data.remote.RecordType

@Composable
fun CategoriesGrid(navController: NavController) {

    val viewModel = hiltViewModel<RecordViewModel>()

    val type by viewModel.type.collectAsState()
    val category by viewModel.category.collectAsState()

    val expenseCategories by viewModel.expenseCategories.collectAsState()
    val incomeCategories by viewModel.incomeCategories.collectAsState()

    FlowRow(
        mainAxisSize = SizeMode.Expand,
        mainAxisSpacing = 8.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        when (type) {
            RecordType.Expense -> expenseCategories.forEach { item ->
                CategoryCard(category = item, isSelected = category == item) {
                    viewModel.setCategory(item)
                }
            }
            RecordType.Income -> incomeCategories.forEach { item ->
                CategoryCard(category = item, isSelected = category == item) {
                    viewModel.setCategory(item)
                }
            }
        }

        EditCategoriesCard {
            navController.navigate(route = "${AddDest.EditCategory.route}/$type")
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

    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = if (isSelected) Color.Yellow else Color.White,
        onClick = onClick
    ) {

        Text(
            text = category,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditCategoriesCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White,
        onClick = onClick
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = stringResource(id = R.string.category_edit_title),
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = stringResource(id = R.string.cta_edit),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}

@Preview
@Composable
private fun CategoryCard_Preview() =
    CategoryCard(category = "Food", isSelected = true, onClick = {})

@Preview
@Composable
private fun EditCategoriesCard_Preview() = EditCategoriesCard(onClick = {})