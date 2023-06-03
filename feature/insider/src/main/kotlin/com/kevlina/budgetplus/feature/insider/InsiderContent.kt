package com.kevlina.budgetplus.feature.insider

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.ui.InfiniteCircularProgress
import com.kevlina.budgetplus.core.ui.Text

@Composable
internal fun InsiderContent() {

    val vm = hiltViewModel<InsiderViewModel>()

    val insiderData by vm.insiderData.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {

        val data = insiderData
        if (data == null) {

            InfiniteCircularProgress(
                modifier = Modifier.padding(top = 32.dp)
            )
        } else {

            Text(text = data.totalUsers.toString())
            Text(text = data.totalPremiumUsers.toString())
            Text(text = data.dailyActiveUsers.toString())

            data.newUsers.forEach { user ->
                Text(text = user.name.orEmpty())
            }
        }
    }

}