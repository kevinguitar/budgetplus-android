package com.kevlina.budgetplus.feature.insider.ui

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Abc
import androidx.compose.material.icons.rounded.Landscape
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.RamenDining
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.InfiniteCircularProgress
import com.kevlina.budgetplus.feature.insider.InsiderViewModel
import com.kevlina.budgetplus.feature.insider.UsersOverviewData

private const val TYPE_LOADER = "loader"
private const val TYPE_STAT = "stat"
private const val TYPE_USER = "user"

@Composable
internal fun BoxWithConstraintsScope.InsiderContent() {

    val vm = hiltViewModel<InsiderViewModel>()

    val insiderData by vm.insiderData.collectAsStateWithLifecycle()

    var showUsersOverview by remember { mutableStateOf(false) }
    var showNewUsers by remember { mutableStateOf(false) }
    var showActivePremiumUsers by remember { mutableStateOf(false) }

    val usersOverviewData = if (showUsersOverview) vm.usersOverviewData.collectAsStateWithLifecycle() else null
    val newUsers = if (showNewUsers) vm.newUsers.collectAsStateWithLifecycle() else null
    val activePremiumUsers = if (showActivePremiumUsers) vm.activePremiumUsers.collectAsStateWithLifecycle() else null

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = AppTheme.listContentPaddings(horizontal = 16.dp, vertical = 12.dp)
    ) {

        val data = insiderData
        if (data == null) {
            item(contentType = TYPE_LOADER) {
                InfiniteCircularProgress(
                    modifier = Modifier.padding(top = 32.dp)
                )
            }
        } else {
            item(contentType = TYPE_STAT) {
                StatCard(
                    title = stringResource(id = R.string.insider_total_premium_users),
                    icon = Icons.Rounded.WorkspacePremium,
                    number = data.totalPremiumUsers
                )
            }

            item(contentType = TYPE_STAT) {
                StatCard(
                    title = stringResource(id = R.string.insider_daily_active_users),
                    icon = Icons.Rounded.Today,
                    number = data.dailyActiveUsers
                )
            }

            item {
                ExpandableTitle(
                    title = stringResource(id = R.string.insider_users_overview),
                    isExpanded = showUsersOverview,
                    onClick = { showUsersOverview = !showUsersOverview }
                )
            }

            if (showUsersOverview) {
                overviewSection(usersOverviewData?.value)
            }

            item {
                ExpandableTitle(
                    title = stringResource(id = R.string.insider_new_users),
                    isExpanded = showNewUsers,
                    onClick = { showNewUsers = !showNewUsers }
                )
            }

            if (showNewUsers) {
                usersSection(newUsers?.value)
            }

            item {
                ExpandableTitle(
                    title = stringResource(id = R.string.insider_active_premium_users),
                    isExpanded = showActivePremiumUsers,
                    onClick = { showActivePremiumUsers = !showActivePremiumUsers }
                )
            }

            if (showActivePremiumUsers) {
                usersSection(activePremiumUsers?.value)
            }
        }
    }
}

private fun LazyListScope.overviewSection(overviewData: UsersOverviewData?) {
    if (overviewData == null) {
        item(contentType = TYPE_LOADER) {
            InfiniteCircularProgress(modifier = Modifier.padding(top = 16.dp))
        }
    } else {
        item(contentType = TYPE_STAT) {
            StatCard(
                title = stringResource(id = R.string.insider_total_users),
                icon = Icons.Rounded.People,
                number = overviewData.totalUsers
            )
        }
        item(contentType = TYPE_STAT) {
            StatCard(
                title = stringResource(id = R.string.insider_total_en_users),
                icon = Icons.Rounded.Abc,
                number = overviewData.totalEnglishUsers
            )
        }
        item(contentType = TYPE_STAT) {
            StatCard(
                title = stringResource(id = R.string.insider_total_ja_users),
                icon = Icons.Rounded.RamenDining,
                number = overviewData.totalJapaneseUsers
            )
        }
        item(contentType = TYPE_STAT) {
            StatCard(
                title = stringResource(id = R.string.insider_total_cn_users),
                icon = Icons.Rounded.Landscape,
                number = overviewData.totalSimplifiedChineseUsers
            )
        }
    }
}

private fun LazyListScope.usersSection(users: List<User>?) {
    if (users == null) {
        item(contentType = TYPE_LOADER) {
            InfiniteCircularProgress(modifier = Modifier.padding(top = 16.dp))
        }
    } else {
        items(
            items = users,
            contentType = { TYPE_USER }
        ) { user ->
            UserCard(user = user)
        }
    }
}