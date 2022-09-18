package com.kevlina.budgetplus.book

import androidx.annotation.DrawableRes
import com.kevlina.budgetplus.R

enum class BookTab(@DrawableRes val icon: Int) {
    Add(R.drawable.ic_record), History(R.drawable.ic_list);

    val route get() = name
}

enum class AddDest {
    Record, EditCategory, UnlockPremium;

    val route get() = name
}

enum class HistoryDest {
    Overview, Details;

    val route get() = name
}