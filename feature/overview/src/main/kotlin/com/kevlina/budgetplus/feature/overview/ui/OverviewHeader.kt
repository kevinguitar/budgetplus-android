package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.RecordTypeTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun OverviewHeader(
    uiState: OverviewHeaderUiState,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {

    val type by uiState.type.collectAsStateWithLifecycle()
    val totalPrice by uiState.totalPrice.collectAsStateWithLifecycle()
    val balance by uiState.balance.collectAsStateWithLifecycle()
    val recordGroups by uiState.recordGroups.collectAsStateWithLifecycle()
    val authors by uiState.authors.collectAsStateWithLifecycle()
    val selectedAuthor by uiState.selectedAuthor.collectAsStateWithLifecycle()

    val isBalanceCardVisible = recordGroups?.isNotEmpty() == true

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {

        RecordTypeTab(
            selected = type,
            onTypeSelected = uiState.setRecordType,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (authors.size > 1) {
            AuthorSelector(
                authors = authors,
                selectedAuthor = selectedAuthor,
                setAuthor = uiState.setAuthor
            )
        }

        TimePeriodSelector(
            uiState = uiState.timePeriodSelectorUiState,
            navigator = navigator,
        )

        AnimatedVisibility(
            visible = isBalanceCardVisible,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically()
        ) {

            BalanceCard(
                totalPrice = totalPrice,
                balance = balance,
            )
        }
    }
}

@Stable
internal data class OverviewHeaderUiState(
    val type: StateFlow<RecordType>,
    val totalPrice: StateFlow<String>,
    val balance: StateFlow<String>,
    val recordGroups: StateFlow<Map<String, List<Record>>?>,
    val authors: StateFlow<List<User>>,
    val selectedAuthor: StateFlow<User?>,
    val timePeriodSelectorUiState: TimePeriodSelectorUiState,
    val setRecordType: (RecordType) -> Unit,
    val setAuthor: (User?) -> Unit,
) {
    companion object {
        val preview = OverviewHeaderUiState(
            type = MutableStateFlow(RecordType.Expense),
            totalPrice = MutableStateFlow("$245.25"),
            balance = MutableStateFlow("$52.45"),
            recordGroups = MutableStateFlow(mapOf("Food" to emptyList())),
            authors = MutableStateFlow(listOf(User(name = "Kevin"), User(name = "Alina"))),
            selectedAuthor = MutableStateFlow(User(name = "Kevin")),
            timePeriodSelectorUiState = TimePeriodSelectorUiState.preview,
            setRecordType = {},
            setAuthor = {}
        )
    }
}

@Preview
@Composable
private fun OverviewHeader_Preview() = AppTheme {
    OverviewHeader(
        uiState = OverviewHeaderUiState.preview,
        navigator = Navigator.empty,
        modifier = Modifier
            .background(LocalAppColors.current.light)
            .padding(horizontal = 16.dp)
    )
}