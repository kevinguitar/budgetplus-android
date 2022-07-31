package com.kevlina.budgetplus.book

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.List
import androidx.compose.ui.graphics.vector.ImageVector

enum class BookTab(val icon: ImageVector) {
    Add(Icons.Rounded.Add), History(Icons.Rounded.List);

    val route get() = name
}

enum class AddDest {
    Record, EditCategory;

    val route get() = name
}

enum class HistoryDest {
    Overview, Details;

    val route get() = name
}