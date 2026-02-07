package com.kevlina.budgetplus.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.settings_title
import com.kevlina.budgetplus.core.common.nav.BookDest
import com.kevlina.budgetplus.core.common.nav.NavController
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.core.utils.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    navController: NavController<BookDest>,
    showMembers: Boolean,
) {

    val vm = metroViewModel<SettingsViewModel>()

    val bookName by vm.bookName.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.light)
    ) {
        TopBar(
            title = stringResource(Res.string.settings_title, bookName.orEmpty()),
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