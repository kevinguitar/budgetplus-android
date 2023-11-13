package com.kevlina.budgetplus.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.TopBar

@Composable
fun SettingsScreen(
    navigator: Navigator,
    showMembers: Boolean,
) {

    val vm = hiltViewModel<SettingsViewModel>()

    val bookName by vm.bookName.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopBar(
            title = stringResource(id = R.string.settings_title, bookName.orEmpty()),
            navigateUp = navigator::navigateUp
        )

        SettingsContent(
            navigator = navigator,
            vm = vm,
            showMembers = showMembers,
            modifier = Modifier
                .width(AppTheme.maxContentWidth)
                .align(Alignment.CenterHorizontally)
                .weight(1F)
        )
    }
}