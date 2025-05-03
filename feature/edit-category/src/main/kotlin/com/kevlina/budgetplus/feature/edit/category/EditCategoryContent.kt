package com.kevlina.budgetplus.feature.edit.category

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Fab
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleShape
import com.kevlina.budgetplus.core.ui.thenIf
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState

// False positive, it's used in context receiver
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun EditCategoryContent(
    modifier: Modifier = Modifier,
    categories: List<String>,
    lazyListState: LazyListState,
    reorderableState: ReorderableLazyListState,
    onDialogEditClick: (CategoryEditMode) -> Unit,
    showEditCategoriesBubble: (BubbleDest.EditCategoriesHint) -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAppColors.current.light)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = AppTheme.listContentPaddings(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                items = categories,
                key = { _, item -> item },
                contentType = { _, _ -> "category" }
            ) { index, item ->

                ReorderableItem(
                    state = reorderableState,
                    key = item,
                ) { isDragging ->

                    CategoryCell(
                        category = item,
                        isDragging = isDragging,
                        onClick = { onDialogEditClick(CategoryEditMode.Rename(item)) },
                        handlerModifier = Modifier.draggableHandle(),
                        modifier = Modifier
                            .longPressDraggableHandle()
                            .thenIf(index == 0) {
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

        Fab(
            icon = Icons.Rounded.Add,
            contentDescription = stringResource(id = R.string.cta_add),
            onClick = { onDialogEditClick(CategoryEditMode.Add) }
        )
    }
}

@PreviewScreenSizes
@Composable
private fun EditCategoryContent_Preview() = AppTheme(themeColors = ThemeColors.NemoSea) {
    val listState = rememberLazyListState()
    EditCategoryContent(
        categories = listOf("Food", "Education", "Daily", "Transport", "Entertainment", "Clothes"),
        lazyListState = listState,
        reorderableState = rememberReorderableLazyListState(listState) { _, _ -> },
        onDialogEditClick = {},
        showEditCategoriesBubble = {}
    )
}