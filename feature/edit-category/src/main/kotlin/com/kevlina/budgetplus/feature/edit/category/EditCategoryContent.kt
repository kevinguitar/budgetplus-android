package com.kevlina.budgetplus.feature.edit.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.Surface
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleShape
import com.kevlina.budgetplus.core.ui.thenIf
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
internal fun EditCategoryContent(
    modifier: Modifier = Modifier,
    categories: List<String>,
    reorderableState: ReorderableLazyListState,
    onDialogEditClick: (CategoryEditMode) -> Unit,
    showEditCategoriesBubble: (BubbleDest.EditCategoriesHint) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAppColors.current.light)
    ) {
        LazyColumn(
            modifier = Modifier
                .width(AppTheme.maxContentWidth)
                .fillMaxHeight()
                .align(Alignment.Center)
                .reorderable(reorderableState)
                .detectReorderAfterLongPress(reorderableState),
            state = reorderableState.listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            itemsIndexed(
                items = categories,
                key = { _, item -> item },
                contentType = { _, _ -> "category" }
            ) { index, item ->

                ReorderableItem(
                    reorderableState = reorderableState,
                    key = item,
                    index = index,
                ) { isDragging ->

                    CategoryCell(
                        category = item,
                        isDragging = isDragging,
                        onClick = { onDialogEditClick(CategoryEditMode.Rename(item)) },
                        handlerModifier = Modifier.detectReorder(reorderableState),
                        modifier = Modifier.thenIf(index == 0) {
                            val bubbleShape = with(LocalDensity.current) {
                                BubbleShape.RoundedRect(AppTheme.cornerRadius.toPx())
                            }

                            Modifier.onPlaced {
                                showEditCategoriesBubble(
                                    BubbleDest.EditCategoriesHint(
                                        size = it.size,
                                        offset = it.positionInRoot(),
                                        shape = bubbleShape
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }

        Surface(
            shape = CircleShape,
            color = LocalAppColors.current.dark,
            onClick = { onDialogEditClick(CategoryEditMode.Add) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(56.dp)
        ) {

            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = stringResource(id = R.string.cta_add),
                tint = LocalAppColors.current.light,
                modifier = Modifier.padding(all = 8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun EditCategoryContent_Preview() = AppTheme(themeColors = ThemeColors.NemoSea) {
    EditCategoryContent(
        categories = listOf("Food", "Education", "Daily", "Transport", "Entertainment", "Clothes"),
        reorderableState = rememberReorderableLazyListState(onMove = { _, _ -> }),
        onDialogEditClick = {},
        showEditCategoriesBubble = {}
    )
}