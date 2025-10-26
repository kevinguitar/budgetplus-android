package com.kevlina.budgetplus.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.BookDest
import com.kevlina.budgetplus.core.common.nav.NavController
import com.kevlina.budgetplus.core.ui.TopBar

@Composable
fun SettingsScreen(
    navController: NavController<BookDest>,
    showMembers: Boolean,
) {

    val vm = hiltViewModel<SettingsViewModel>()

    val bookName by vm.bookName.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopBar(
            title = stringResource(id = R.string.settings_title, bookName.orEmpty()),
            navigateUp = navController::navigateUp
        )

        SettingsContent(
            navController = navController,
            vm = vm,
            showMembers = showMembers,
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
        )
    }
}