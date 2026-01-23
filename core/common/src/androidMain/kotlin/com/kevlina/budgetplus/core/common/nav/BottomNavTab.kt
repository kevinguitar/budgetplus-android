package com.kevlina.budgetplus.core.common.nav

enum class BottomNavTab {
    Add, History;

    val root
        get() = when (this) {
            Add -> BookDest.Record
            History -> BookDest.Overview
        }
}