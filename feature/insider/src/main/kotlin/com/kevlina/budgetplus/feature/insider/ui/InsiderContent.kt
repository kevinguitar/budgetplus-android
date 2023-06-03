package com.kevlina.budgetplus.feature.insider.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.InfiniteCircularProgress
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.feature.insider.InsiderViewModel

private const val TYPE_LOADER = "loader"
private const val TYPE_STAT = "stat"
private const val TYPE_USER = "user"

@Composable
internal fun InsiderContent() {

    val vm = hiltViewModel<InsiderViewModel>()

    val insiderData by vm.insiderData.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp)
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
                    title = stringResource(id = R.string.insider_total_users),
                    icon = Icons.Rounded.People,
                    number = data.totalUsers
                )
            }

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
                Text(
                    text = stringResource(id = R.string.insider_new_users),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = FontSize.Large,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp)
                )
            }

            items(
                items = data.newUsers,
                contentType = { TYPE_USER }
            ) { user ->
                UserCard(user = user)
            }
        }
    }
}