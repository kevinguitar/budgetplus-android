package com.kevingt.moneybook.book

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

enum class BookTab(val icon: ImageVector) {
    Add(Icons.Filled.Add), History(Icons.Filled.List);

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